package com.uwhy.helper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.uwhy.helper.lazylist.LazyListWrapper

class ItemFragment : Fragment() {
    companion object {
        const val POSITION_KEY = "POSITION_KEY"
        const val IS_HOR_KEY = "IS_HOR_KEY"
        const val WITH_WRAPPER_KEY = "WITH_WRAPPER_KEY"
        private val bgArray = arrayOf(Color.LightGray, Color.Magenta, Color.DarkGray, Color.Cyan)

        fun newInstance(position: Int, isHor: Boolean, withWrapper: Boolean): ItemFragment {
            val args = Bundle()
            args.putInt(POSITION_KEY, position)
            args.putBoolean(IS_HOR_KEY, isHor)
            args.putBoolean(WITH_WRAPPER_KEY, withWrapper)
            val fragment = ItemFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val position: Int by lazy {
        arguments?.getInt(POSITION_KEY, 0) ?: 0
    }
    private val isHor: Boolean by lazy {
        arguments?.getBoolean(IS_HOR_KEY, true) ?: true
    }
    private val withWrapper: Boolean by lazy {
        arguments?.getBoolean(WITH_WRAPPER_KEY, true) ?: true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(inflater.context).apply {
            setContent {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(bgArray[position % (bgArray.size)])
                ) {
                    Text(text = "ViewPager Item $position", fontSize = 18.sp, color = Color.White)

                    val listContent: LazyListScope.() -> Unit = {
                        for (i in 0 until 10) {
                            item { Item(i) }
                        }
                    }

                    if (withWrapper) {
                        LazyListWrapper {
                            if (isHor)
                                LazyRow(
                                    verticalAlignment = Alignment.CenterVertically,
                                    state = wrapperListState,
                                    content = listContent
                                )
                            else
                                LazyColumn(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    state = wrapperListState,
                                    content = listContent
                                )
                        }
                    } else {
                        if (isHor)
                            LazyRow(
                                verticalAlignment = Alignment.CenterVertically,
                                content = listContent
                            )
                        else
                            LazyColumn(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                content = listContent
                            )
                    }

                }
            }
        }
    }

    @Composable
    fun Item(position: Int) {
        Box(
            modifier = Modifier
                .padding(15.dp)
                .height(50.dp)
                .width(130.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "LazyRow Item $position", fontSize = 14.sp, color = Color.Black)
        }
    }
}