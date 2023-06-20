package me.ako.yts.presentation.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import me.ako.yts.R
import me.ako.yts.data.datasource.model.MovieDetailEntity
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

    private var url: String? = null
    private var torrentUrl: String? = null
    private var torrentTitle: String? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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

        viewModel.refreshMovie(args.movieId)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            appViewModel = viewModel

            viewModel.loadMovie(args.movieId).observe(viewLifecycleOwner) { movie ->
                if (movie != null) {
                    this.movie = movie

                    url = movie.url

                    movie.runtime?.let {
                        val year = movie.year?.toString()
                        val rated = if (movie.mpa_rating.isNullOrEmpty()) {
                            "Unrated"
                        } else {
                            movie.mpa_rating?.uppercase()
                        }
                        val runtime = "${it / 60}h ${it % 60}m"
                        txtYear.text = SpannableString("$year \u2022 $rated \u2022 $runtime")
                    }

                    txtGenre.text = movie.genres?.joinToString(separator = " / ")
                    txtImdbRating.text = movie.rating?.toString()

                    movie.like_count?.let {
                        txtLikeCount.text = utils.shortenNumber(it, 10)
                    }
                    movie.download_count?.let {
                        txtDownloadCount.text = utils.shortenNumber(it, 10)
                    }

                    imgBackground.load(movie.image?.backgroundImageOriginal) {
                        crossfade(true)
                        error(ColorDrawable(Color.LTGRAY))
                    }

                    imgCover.load(movie.image?.mediumCoverImage) {
                        crossfade(true)
                        error(ColorDrawable(Color.LTGRAY))
                    }

                    imgCover.setOnClickListener {
                        navigateToImageView(movie, 0)
                    }

                    val uploaded = "Uploaded: ${movie.date_uploaded}"
                    txtUploadedDate.text = uploaded

                    txtImdbRating.setOnClickListener {
                        if (!movie.imdb_code.isNullOrEmpty()) {
                            requireActivity().startActivity(utils.imdbTitle(movie.imdb_code))
                        } else {
                            utils.snack(binding.root, "IMDB link not available")
                        }
                    }

                    txtYoutube.setOnClickListener {
                        if (!movie.yt_trailer_code.isNullOrEmpty()) {
                            requireActivity().startActivity(utils.youtube(movie.yt_trailer_code))
                        } else {
                            utils.snack(binding.root, "YouTube link not available")
                        }
                    }

                    btnDownload.setOnClickListener {
                        downloadDialog(movie)
                    }

                    btnDownloadSubtitle.setOnClickListener {
                        requireActivity().startActivity(utils.subtitle(movie.imdb_code))
                    }

                    setupCast(movie)
                    setupTechSpecs(movie)
                    setupScreenshot(movie)
                }
            }

            viewModel.statusMovieDetail.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Done -> {
                        if (!layoutMovieDetail.isVisible) {
                            layoutMovieDetail.visibility = View.VISIBLE
                        }

                        if (layoutNoMovie.isVisible) {
                            layoutNoMovie.visibility = View.GONE
                        }

                        //layoutEmpty.visibility = View.GONE
                    }

                    is ApiStatus.Error -> {
                        if (layoutMovieDetail.isVisible) {
                            layoutMovieDetail.visibility = View.GONE
                        }

                        if (!layoutNoMovie.isVisible) {
                            layoutNoMovie.visibility = View.VISIBLE
                        }

                        //layoutEmpty.visibility = View.GONE
                    }

                    is ApiStatus.Loading -> {
                        //layoutMovieDetail.visibility = View.GONE
                        //layoutEmpty.visibility = View.VISIBLE
                    }
                }
            }

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_share -> {
                        if (!url.isNullOrEmpty()) {
                            requireActivity().startActivity(utils.shareText(url))
                        }
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

    private fun navigateToImageView(movie: MovieDetailEntity, current: Int) {
        movie.image?.let { image ->
            val urls = arrayOf(
                image.largeCoverImage!!,
                image.largeScreenshotImage1!!,
                image.largeScreenshotImage2!!,
                image.largeScreenshotImage3!!
            )
            val action =
                FragmentMovieDetailDirections.actionFragmentMovieDetailToFragmentImageView(
                    urls, current
                )
            findNavController().navigate(action)
        }
    }

    private fun downloadDialog(movie: MovieDetailEntity) {
        movie.torrents?.let { list ->

            val downloadEntries = list.map { torrent ->
                "${torrent.quality}.${torrent.type?.replaceFirstChar { it.uppercase() }}"
            }.toTypedArray()

            val checkedItem = 0

            torrentUrl = list[checkedItem].url
            torrentTitle =
                "${movie.title?.titleLong} [${list[checkedItem].quality}] " +
                        "[${list[checkedItem].type?.replaceFirstChar { it.uppercase() }}] " +
                        "[YTS.MX].torrent"

            var movieName = "${movie.title?.titleLong?.replace(" ","+")}+[${list[checkedItem].quality}]+[YTS.MX]"
            var magnetUrl = "magnet:?xt=urn:btih:${list[checkedItem].hash}&dn=${movieName}" +
                    "&tr=udp://glotorrents.pw:6969/announce" +
                    "&tr=udp://tracker.opentrackr.org:1337/announce" +
                    "&tr=udp://torrent.gresille.org:80/announce" +
                    "&tr=udp://tracker.openbittorrent.com:80" +
                    "&tr=udp://tracker.coppersurfer.tk:6969" +
                    "&tr=udp://tracker.leechers-paradise.org:6969" +
                    "&tr=udp://p4p.arenabg.ch:1337" +
                    "&tr=udp://tracker.internetwarriors.net:1337"
            Log.d("FragmentMovieDetail", "magnetUrl: $magnetUrl")
            Log.d("FragmentMovieDetail", "magnetUrlLength: ${magnetUrl.length}")

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Torrents")
                .setSingleChoiceItems(downloadEntries, checkedItem) { v, which ->
                    torrentUrl = list[which].url
                    torrentTitle =
                        "${movie.title?.titleLong} [${list[which].quality}] " +
                                "[${list[which].type?.replaceFirstChar { it.uppercase() }}] " +
                                "[YTS.MX].torrent"

                    movieName = "${movie.title?.titleLong?.replace(" ","+")}+[${list[which].quality}]+[YTS.MX]"
                    magnetUrl = "magnet:?xt=urn:btih:${list[which].hash}&dn=${movieName}" +
                            "&tr=udp://glotorrents.pw:6969/announce" +
                            "&tr=udp://tracker.opentrackr.org:1337/announce" +
                            "&tr=udp://torrent.gresille.org:80/announce" +
                            "&tr=udp://tracker.openbittorrent.com:80" +
                            "&tr=udp://tracker.coppersurfer.tk:6969" +
                            "&tr=udp://tracker.leechers-paradise.org:6969" +
                            "&tr=udp://p4p.arenabg.ch:1337" +
                            "&tr=udp://tracker.internetwarriors.net:1337"
                }
                .setNegativeButton("Magnet") { v, which ->
                    utils.magnet(magnetUrl)

                    v.dismiss()
                }
                .setPositiveButton("Download") { v, which ->
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        if (ContextCompat.checkSelfPermission(
                                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            downloadTorrent()
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }

                    v.dismiss()
                }.show()
        }
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

    private fun setupCast(movie: MovieDetailEntity) {
        movie.cast?.let { list ->
            binding.apply {
                val castAdapter = CastAdapter(list) { cast ->
                    if (!cast.imdb_code.isNullOrEmpty()) {
                        requireActivity().startActivity(utils.imdbName(cast.imdb_code))
                    } else {
                        utils.snack(binding.root, "IMDB link not available")
                    }
                }
                recyclerCast.adapter = castAdapter
            }
        }
    }

    private fun setupTechSpecs(movie: MovieDetailEntity) {
        movie.torrents?.let { list ->
            binding.apply {
                viewPagerTechSpecs.adapter = TorrentAdapter(list)
                TabLayoutMediator(tabsTechSpecs, viewPagerTechSpecs) { tab, position ->
                    val title = "${list[position].quality}." +
                            list[position].type?.replaceFirstChar { it.uppercase() }
                    tab.text = title
                }.attach()
            }
        }
    }

    private fun setupScreenshot(movie: MovieDetailEntity) {
        binding.apply {
            movie.image?.let { image ->
                val screenshots = listOf(
                    image.largeScreenshotImage1,
                    image.largeScreenshotImage2,
                    image.largeScreenshotImage3
                )
                viewPagerScreenshot.adapter = ScreenshotAdapter(screenshots) {
                    val i = screenshots.indexOf(it)
                    navigateToImageView(movie, i + 1)
                }
                pageIndicator.setupWithViewPager(viewPagerScreenshot)
            }
        }
    }

    private fun downloadMagnet() {

    }

    private fun downloadTorrent() {
        if (!torrentUrl.isNullOrEmpty() && !torrentTitle.isNullOrEmpty()) {
            downloader.downloadTorrent(torrentUrl!!, torrentTitle!!)
        }
    }
}