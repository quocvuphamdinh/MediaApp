package com.example.mediaapp.features.sharewithme.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaapp.R
import com.example.mediaapp.databinding.FragmentMusicShareWithMeBinding
import com.example.mediaapp.features.adapters.DirectoryAdapter
import com.example.mediaapp.features.adapters.FileAdapter
import com.example.mediaapp.features.sharewithme.ShareWithMeViewModel
import com.example.mediaapp.features.sharewithme.ShareWithMeViewModelFactory
import com.example.mediaapp.features.util.BottomSheetOptionFragment
import com.example.mediaapp.models.Directory
import com.example.mediaapp.models.File
import com.example.mediaapp.util.Constants
import com.example.mediaapp.features.MediaApplication

class ShareWithMeMusicFragment : Fragment() {
    private lateinit var binding: FragmentMusicShareWithMeBinding
    private lateinit var folderAdapter: DirectoryAdapter
    private lateinit var fileAdapter: FileAdapter
    private val viewModel: ShareWithMeViewModel by activityViewModels {
        ShareWithMeViewModelFactory((activity?.application as MediaApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicShareWithMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViewFolder()
        setUpRecyclerViewFile()
        setUpLoadMoreInRecyclerView()
        subscribeToObservers()
    }
    private fun showBottomSheetOption(any: Any){
        BottomSheetOptionFragment(any is Directory, Constants.SHARE_WITH_ME).apply {
            when(any){
                is Directory -> setTitleName(any.name)
                is File -> setTitleName(any.name)
            }
            setClickAddToFavorite {
                viewModel.setDirectoryLongClick(any, 1)
                closeBottomSheet()
            }
            setClickDelete {
                viewModel.setDirectoryLongClick(any, 2)
                closeBottomSheet()
            }
        }.show(parentFragmentManager, Constants.BOTTOM_SHEET_OPTION_TAG)
    }
    private fun setUpLoadMoreInRecyclerView() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshFoldersAndFiles(2)
        }
        binding.rcvShareWithMeFolderMusic.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)){
                    viewModel.loadMore(viewModel.pageMusic+1, 2, true)
                }
            }
        })
        binding.rcvShareWithMeFileMusic.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(!recyclerView.canScrollVertically(1)){
                    viewModel.loadMore(viewModel.pageMusicFile+1, 2, false)
                }
            }
        })
    }
    private fun subscribeToObservers() {
        viewModel.isHaveMoreMusicsFile.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.pageMusicFile++
            }else{
                binding.rcvShareWithMeFileMusic.setPadding(0,0,0,0)
            }
        })
        viewModel.isHaveMoreMusics.observe(viewLifecycleOwner, Observer {
            if(it){
                viewModel.pageMusic++
            }else{
                binding.rcvShareWithMeFolderMusic.setPadding(0,0,0,0)
            }
        })
        viewModel.folderMusics.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                binding.swipeRefreshLayout.isRefreshing = false
            }
            folderAdapter.submitList(it)
        })
        viewModel.fileMusics.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                binding.swipeRefreshLayout.isRefreshing = false
            }
            fileAdapter.submitList(it)
        })
    }

    private fun setUpRecyclerViewFile() {
        fileAdapter = FileAdapter(object : FileAdapter.CLickItemDirectory {
            override fun clickItem(file: File) {
                findNavController().navigate(R.id.action_shareWithMeFragment_to_musicDetailFragment)
            }

            override fun longClickItem(file: File) {
                showBottomSheetOption(file)
            }
        })
        binding.rcvShareWithMeFileMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvShareWithMeFileMusic.adapter = fileAdapter
    }

    private fun setUpRecyclerViewFolder() {
        folderAdapter = DirectoryAdapter(object : DirectoryAdapter.CLickItemDirectory {
            override fun clickItem(directory: Directory) {
                goToDirectoryDetail(directory)
            }

            override fun longClickItem(directory: Directory) {
                showBottomSheetOption(directory)
            }
        })
        binding.rcvShareWithMeFolderMusic.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvShareWithMeFolderMusic.adapter = folderAdapter
    }
    private fun goToDirectoryDetail(directory: Directory){
        val bundle = Bundle()
        bundle.putString(Constants.DIRECTORY_ID, directory.id.toString())
        bundle.putString(Constants.DIRECTORY_NAME, directory.name)
        bundle.putInt(Constants.DIRECTORY_LEVEL, directory.level)
        bundle.putInt(Constants.ROOT_TYPE, Constants.SHARE_WITH_ME)
        findNavController().navigate(
            R.id.action_shareWithMeFragment_to_directoryDetailFragment2,
            bundle
        )
    }
}