package org.schulcloud.mobile.controllers.file

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_file.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.schulcloud.mobile.R
import org.schulcloud.mobile.controllers.course.CourseFragmentArgs
import org.schulcloud.mobile.controllers.main.MainFragment
import org.schulcloud.mobile.controllers.main.MainFragmentConfig
import org.schulcloud.mobile.databinding.FragmentFileBinding
import org.schulcloud.mobile.models.course.CourseRepository
import org.schulcloud.mobile.models.file.FileRepository
import org.schulcloud.mobile.models.user.Permission
import org.schulcloud.mobile.models.user.UserRepository
import org.schulcloud.mobile.models.user.hasPermission
import org.schulcloud.mobile.utils.*
import org.schulcloud.mobile.viewmodels.FileViewModel
import org.schulcloud.mobile.viewmodels.IdViewModelFactory
import java.io.File as JavaFile


class FileFragment : MainFragment<FileFragment, FileViewModel>() {
    private val args: FileFragmentArgs by lazy {
        FileFragmentArgs.fromBundle(arguments)
    }

    private val directoryAdapter: DirectoryAdapter by lazy {
        DirectoryAdapter {
            navController.navigate(R.id.action_global_fragment_file,
                    FileFragmentArgs.Builder(combinePath(viewModel.path, it)).build().toBundle())
        }
    }
    private val fileAdapter: FileAdapter by lazy {
        FileAdapter({ launch { downloadFile(it, false) } },
                { launch { downloadFile(it, true) } })
    }


    override var url: String? = null
        get() {
            val parts = args.path.getPathParts()
            val path = if (parts.size <= 2) ""
            else "?dir=${parts.takeLast(parts.size - 2).combinePath().ensureSlashes()}"

            return when (parts.first()) {
                FileRepository.CONTEXT_MY_API -> "/files/my/$path"
                FileRepository.CONTEXT_COURSES -> "/files/courses/${parts[1]}$path"
                else -> null
            }
        }

    override fun provideSelfConfig() = viewModel.course
            .combineLatestBothNullable(viewModel.currentUser)
            .map { (course, user) ->
                breadcrumbs.setPath(args.path, course)
                val parts = args.path.getPathParts()

                MainFragmentConfig(
                        title = when {
                            parts.size > 2 -> parts.last()
                            parts.first() == FileRepository.CONTEXT_MY_API ->
                                context?.getString(R.string.file_directory_my)
                            parts.first() == FileRepository.CONTEXT_COURSES ->
                                course?.name ?: context?.getString(R.string.file_directory_course_unknown)
                            else -> context?.getString(R.string.file_directory_unknown)
                        },
                        showTitle = false,
                        toolbarColor = course?.color?.let { Color.parseColor(it) },
                        menuBottomRes = listOf(R.menu.fragment_file_bottom),
                        menuBottomHiddenIds = listOf(
                                R.id.file_action_gotoCourse.takeIf { course == null }
                        ),
                        fabIconRes = R.drawable.ic_file_upload_white_24dp,
                        fabVisible = user.hasPermission(Permission.FILE_CREATE)
                )
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, IdViewModelFactory(args.path))
                .get(FileViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentFileBinding.inflate(layoutInflater).also {
            it.viewModel = viewModel
            it.setLifecycleOwner(this)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun updateEmptyMessage() {
            val directoriesEmpty = viewModel.directories.value?.isEmpty() ?: true
            val filesEmpty = viewModel.files.value?.isEmpty() ?: true

            empty.visibilityBool = directoriesEmpty && filesEmpty
            directoriesHeader.visibilityBool = !directoriesEmpty
            filesHeader.visibilityBool = !filesEmpty
        }

        viewModel.directories.observe(this, Observer {
            directoryAdapter.update(it ?: emptyList())
            updateEmptyMessage()
        })
        directories_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = directoryAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        viewModel.files.observe(this, Observer {
            fileAdapter.update(it ?: emptyList())
            updateEmptyMessage()
        })
        files_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fileAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onResume() {
        super.onResume()

        mainActivity.setToolbarWrapper(toolbarWrapper)

        breadcrumbs.onPathSelected = callback@{ path ->
            if (path == args.path) {
                performRefresh()
                return@callback
            }

            navController.navigate(R.id.action_global_fragment_file,
                    FileFragmentArgs.Builder(path).build().toBundle())
        }
        mainViewModel.toolbarColors.observe(this, Observer {
            breadcrumbs.textColor = it.textColor
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.file_action_gotoCourse -> viewModel.courseId?.also { id ->
                navController.navigate(R.id.action_global_fragment_course,
                        CourseFragmentArgs.Builder(id).build().toBundle())
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onFabClicked() {
        launch(UI) {
            if (!requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                getContext()!!.showGenericError(R.string.file_pick_error_readPermissionDenied)
                return@launch
            }

            val res = startActivityForResult(createFilePickerIntent() ?: return@launch)
            if (!res.success) return@launch

            getContext()!!.uploadFile(res.data?.data, viewModel.path)
        }
    }

    override suspend fun refresh() {
        FileRepository.syncDirectory(viewModel.path)
        viewModel.courseId?.also {
            CourseRepository.syncCourse(it)
        }
        UserRepository.syncCurrentUser()
    }
}
