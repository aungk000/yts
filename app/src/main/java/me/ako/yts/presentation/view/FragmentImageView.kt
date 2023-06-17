package me.ako.yts.presentation.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import me.ako.yts.R
import me.ako.yts.data.network.model.MovieDetail
import me.ako.yts.databinding.FragmentImageViewBinding
import me.ako.yts.domain.util.FileDownloader
import me.ako.yts.domain.util.Utils
import me.ako.yts.presentation.presenter.ImageViewAdapter
import javax.inject.Inject

@AndroidEntryPoint
class FragmentImageView : Fragment() {
    private var _binding: FragmentImageViewBinding? = null
    private val binding get() = _binding!!
    private val args: FragmentImageViewArgs by navArgs()

    @Inject
    lateinit var downloader: FileDownloader

    @Inject
    lateinit var utils: Utils

    private var url = ""
    private var title = ""
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    downloader.downloadImage(url, title)
                } else {
                    utils.snack(binding.root, "External storage permission is not granted")
                }
            }

        requireActivity().apply {
            window.navigationBarColor = Color.TRANSPARENT
            //window.statusBarColor = Color.TRANSPARENT

            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, window.decorView).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        //(requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(requireView()) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            /*binding.fabSave.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = insets.bottom
            }*/

            WindowInsetsCompat.CONSUMED
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            viewPagerImage.adapter = ImageViewAdapter(args.urls)
            viewPagerImage.setCurrentItem(args.current, false)

            /*toolbar.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_save -> {
                        save()
                        true
                    }

                    else -> false
                }
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }*/

            /*fabSave.setOnClickListener {
                url = urls[viewPagerImage.currentItem]
                val split = url.substringAfter("movies/").split("/")
                title = "${split[0]}-${split[1]}"

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        downloader.downloadImage(url, title)
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }
            }*/
        }
    }

    private fun save() {
        url = args.urls[binding.viewPagerImage.currentItem]
        val split = url.substringAfter("movies/").split("/")
        title = "${split[0]}-${split[1]}"

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                downloader.downloadImage(url, title)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        requireActivity().apply {
            window.navigationBarColor = android.R.attr.colorBackground
            //window.statusBarColor = android.R.attr.colorBackground

            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, window.decorView).apply {
                show(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }

        //(requireActivity() as AppCompatActivity).supportActionBar?.show()
    }
}