package com.uwhy.helper.lazylist

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ScrollingView
import androidx.viewpager.widget.ViewPager
import com.uwhy.helper.lazylist.LazyListWrapperLayout.OrientationMode.Companion.HORIZONTAL
import com.uwhy.helper.lazylist.LazyListWrapperLayout.OrientationMode.Companion.UNSET
import com.uwhy.helper.lazylist.LazyListWrapperLayout.OrientationMode.Companion.VERTICAL
import kotlin.math.absoluteValue

@Stable
data class LazyListWrapperScope(val wrapperListState: LazyListState)

/**
 * remember use wrapperListState in LazyList
 */
@Composable
fun LazyListWrapper(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    content: @Composable @UiComposable LazyListWrapperScope.() -> Unit
) {
    AndroidView(
        factory = { context -> LazyListWrapperLayout(context) },
        modifier = modifier
    ) { view ->
        (view as? LazyListWrapperLayout)?.let { layout ->
            layout.content.setContent {
                val scope = LazyListWrapperScope(state)
                view.listState = state
                scope.content()
            }
        }
    }
}

/**
 * @author uwhy
 */
private class LazyListWrapperLayout : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(HORIZONTAL, VERTICAL, UNSET)
    annotation class OrientationMode {
        companion object {
            const val HORIZONTAL: Int = 0
            const val VERTICAL: Int = 1
            const val UNSET: Int = -1
        }
    }

    @OrientationMode
    private var orientation: Int = UNSET
    var listState: LazyListState? = null
    val content = ComposeView(context = context)
    private var traditionalParent: ViewParent? = null

    init {
        addView(content, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        searchTraditionalParent()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        traditionalParent = null
        orientation = UNSET
    }

    private fun searchTraditionalParent() {
        var tempParent: ViewParent = parent
        while (tempParent.parent != null && tempParent.parent is View) {
            tempParent = tempParent.parent
            if (tempParent is AbstractComposeView) continue
            if (tempParent is ScrollingView || tempParent is ViewPager) {
                orientation = getScrollingViewOrientation(tempParent as View)
                traditionalParent = tempParent
                return
            }
        }
    }

    private fun getScrollingViewOrientation(view: View): Int = when (view) {
        is ScrollingView -> if (view.computeHorizontalScrollRange() > 0) HORIZONTAL else if (view.computeVerticalScrollRange() > 0) VERTICAL else UNSET
        is ViewPager -> HORIZONTAL
        else -> UNSET
    }

    override fun canScrollHorizontally(direction: Int): Boolean = when (direction) {
        -1 -> listState?.canScrollBackward ?: false
        1 -> listState?.canScrollForward ?: false
        else -> false
    }

    override fun canScrollVertically(direction: Int): Boolean = when (direction) {
        -1 -> listState?.canScrollBackward ?: false
        1 -> listState?.canScrollForward ?: false
        else -> false
    }

    private var initialX = 0f
    private var initialY = 0f
    override fun dispatchTouchEvent(e: MotionEvent?): Boolean {
        e ?: return super.dispatchTouchEvent(e)

        val scrollParent = traditionalParent ?: return super.dispatchTouchEvent(e)
        if (orientation == UNSET) {
            val scrollOrientation = getScrollingViewOrientation(scrollParent as View)
            if (scrollOrientation == UNSET) return super.dispatchTouchEvent(e)
            orientation = scrollOrientation
        }

        if (e.action == MotionEvent.ACTION_DOWN) {
            initialX = e.x
            initialY = e.y
            scrollParent.requestDisallowInterceptTouchEvent(true)
            return super.dispatchTouchEvent(e)
        }

        val dx = e.x - initialX
        val dy = e.y - initialY
        val absoluteDx = dx.absoluteValue
        val absoluteDy = dy.absoluteValue

        if (orientation == HORIZONTAL && absoluteDx > 0 && absoluteDx > absoluteDy) {
            if (canScrollHorizontally(if (dx < 0) 1 else -1))
                scrollParent.requestDisallowInterceptTouchEvent(true)
            else
                scrollParent.requestDisallowInterceptTouchEvent(false)
        } else if (orientation == VERTICAL && absoluteDy > 0 && absoluteDx < absoluteDy) {
            if (canScrollVertically(if (dy < 0) 1 else -1))
                scrollParent.requestDisallowInterceptTouchEvent(true)
            else
                scrollParent.requestDisallowInterceptTouchEvent(false)
        }

        return super.dispatchTouchEvent(e)
    }
}