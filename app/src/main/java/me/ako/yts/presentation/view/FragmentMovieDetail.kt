package me.ako.yts.presentation.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import me.ako.yts.R
import me.ako.yts.data.datasource.model.MovieEntity
import me.ako.yts.data.network.ApiStatus
import me.ako.yts.databinding.FragmentMovieDetailBinding
import me.ako.yts.domain.util.FileDownloader
import me.ako.yts.domain.util.Utils
import me.ako.yts.domain.viewmodel.AppViewModel
import me.ako.yts.presentation.presenter.CastAdapter
import me.ako.yts.presentation.presenter.MovieSuggestionAdapter
import me.ako.yts.presentation.presenter.ScreenshotAdapter
import me.ako.yts.presentation.presenter.TorrentAdapter
import javax.inject.Inject

@AndroidEntryPoint
class FragmentMovieDetail : Fragment() {
    private val viewModel: AppViewModel by activityViewModels()
    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private val args: FragmentMovieDetailArgs by navArgs()

    @Inject
    lateinit var utils: Utils

    @Inject
    lateinit var downloader: FileDownloader

    private var url = ""
    private var torrentUrl = ""
    private var torrentTitle = ""
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.refreshMovie(args.movieId)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    downloadTorrent()
                } else {
                    utils.snack(binding.root, "External storage permission is not granted")
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            appViewModel = viewModel

            viewModel.loadMovie(args.movieId).observe(viewLifecycleOwner) { movie ->
                this.movie = movie

                url = movie.url!!

                val year = movie.year.toString()
                val language = "[${movie.language?.uppercase()}]"
                val runtime = "${movie.runtime!! / 60}h ${movie.runtime % 60}m"
                val span = SpannableString("$year \u2022 $language \u2022 $runtime")
                txtYear.text = span

                txtGenre.text = movie.genres?.joinToString(separator = " / ")
                txtLikeCount.text = utils.shortenNumber(movie.like_count!!, 10)
                txtDownloadCount.text = utils.shortenNumber(movie.download_count!!, 10)
                txtImdbRating.text = movie.rating.toString()

                imgCover.load(movie.medium_cover_image) {
                    crossfade(true)
                    error(ColorDrawable(Color.LTGRAY))
                }

                imgCover.setOnClickListener {
                    navigateToImageView(movie, 0)
                }

                txtDescription.setOnClickListener {
                    if (txtDescription.lineCount > 5) {
                        txtDescription.maxLines = 5
                        txtDescription.ellipsize = TextUtils.TruncateAt.END
                    } else {
                        txtDescription.maxLines = Integer.MAX_VALUE
                        txtDescription.ellipsize = null
                    }
                }

                val uploaded = "Uploaded: ${movie.date_uploaded}"
                txtUploadedDate.text = uploaded

                txtImdbRating.setOnClickListener {
                    if (movie.imdb_code!!.isNotBlank()) {
                        requireActivity().startActivity(utils.imdbTitle(movie.imdb_code))
                    } else {
                        utils.snack(binding.root, "IMDB link not available")
                    }
                }

                txtYoutube.setOnClickListener {
                    if (movie.yt_trailer_code!!.isNotBlank()) {
                        requireActivity().startActivity(utils.youtube(movie.yt_trailer_code))
                    } else {
                        utils.snack(binding.root, "YouTube link not available")
                    }
                }

                btnDownload.setOnClickListener {
                    downloadDialog(movie)
                }

                setupCast(movie)
                setupTechSpecs(movie)
                setupScreenshot(movie)
            }

            viewModel.statusMovieDetail.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Done -> {
                        layoutMovieDetail.visibility = View.VISIBLE
                        //layoutEmpty.visibility = View.GONE
                    }

                    is ApiStatus.Error -> {
                        layoutMovieDetail.visibility = View.GONE
                        //layoutEmpty.visibility = View.GONE
                        utils.snack(binding.root, it.message!!)
                    }

                    is ApiStatus.Loading -> {
                        layoutMovieDetail.visibility = View.GONE
                        //layoutEmpty.visibility = View.VISIBLE
                    }
                }
            }

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_share -> {
                        requireActivity().startActivity(utils.shareText(url))
                        true
                    }

                    else -> false
                }
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            setupMovieSuggestion()
        }
    }

    private fun navigateToImageView(movie: MovieEntity, current: Int) {
        val urls = arrayOf(
            movie.large_cover_image!!,
            movie.large_screenshot_image1!!,
            movie.large_screenshot_image2!!,
            movie.large_screenshot_image3!!
        )
        val action = FragmentMovieDetailDirections.actionFragmentMovieDetailToFragmentImageView(
            urls,
            current
        )
        findNavController().navigate(action)
    }

    private fun downloadDialog(movie: MovieEntity) {
        val downloadEntries = movie.torrents?.map { t ->
            "${t.quality}.${t.type.replaceFirstChar { it.uppercase() }}"
        }?.toTypedArray()

        val checkedItem = 0

        torrentUrl = movie.torrents!![checkedItem].url
        torrentTitle = "${movie.title_long} [${movie.torrents[checkedItem].quality}] " +
                "[${movie.torrents[checkedItem].type.replaceFirstChar { it.uppercase() }}] " +
                "[YTS.MX].torrent"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Torrents")
            .setSingleChoiceItems(downloadEntries, checkedItem) { v, which ->
                torrentUrl = movie.torrents[which].url
                torrentTitle = "${movie.title_long} [${movie.torrents[which].quality}] " +
                        "[${movie.torrents[which].type.replaceFirstChar { it.uppercase() }}] " +
                        "[YTS.MX].torrent"
            }
            .setPositiveButton("Download") { v, which ->
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        downloadTorrent()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }

                v.dismiss()
            }
            .show()
    }

    private fun setupMovieSuggestion() {
        binding.apply {
            val adapter = MovieSuggestionAdapter {
                val action =
                    FragmentMovieDetailDirections.actionFragmentMovieDetailSelf(it.id, it.title)
                findNavController().navigate(action)
            }
            recyclerMovieSuggestions.adapter = adapter

            viewModel.loadMovieSuggestions(args.movieId).observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
        }
    }

    private fun setupCast(movie: MovieEntity) {
        binding.apply {
            val castAdapter = CastAdapter(movie.cast!!) { cast ->
                if (cast.imdb_code.isNotBlank()) {
                    requireActivity().startActivity(utils.imdbName(cast.imdb_code))
                } else {
                    utils.snack(binding.root, "IMDB link not available")
                }
            }
            recyclerCast.adapter = castAdapter
        }
    }

    private fun setupTechSpecs(movie: MovieEntity) {
        binding.apply {
            viewPagerTechSpecs.adapter = TorrentAdapter(movie.torrents!!)
            TabLayoutMediator(tabsTechSpecs, viewPagerTechSpecs) { tab, position ->
                val title =
                    "${movie.torrents[position].quality}.${movie.torrents[position].type.replaceFirstChar { it.uppercase() }}"
                tab.text = title
            }.attach()
        }
    }

    private fun setupScreenshot(movie: MovieEntity) {
        binding.apply {
            val screenshots = listOf(
                movie.large_screenshot_image1,
                movie.large_screenshot_image2,
                movie.large_screenshot_image3
            )
            viewPagerScreenshot.adapter = ScreenshotAdapter(screenshots) {
                val i = screenshots.indexOf(it)
                navigateToImageView(movie, i + 1)
            }
            pageIndicator.setupWithViewPager(viewPagerScreenshot)
        }
    }

    private fun downloadTorrent() {
        downloader.downloadTorrent(torrentUrl, torrentTitle)
    }
}