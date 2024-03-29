package com.example.pokewiki.utils

import android.transition.TransitionManager
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class ConstraintUtils(private val constraintLayout: ConstraintLayout) {
    private var begin: ConstraintBegin? = null
    private val applyConstraintSet = ConstraintSet()
    private val resetConstraintSet = ConstraintSet()

    /**
     * 开始修改
     * @return
     */
    private fun begin(): ConstraintBegin {
        synchronized(ConstraintBegin::class.java) {
            if (begin == null) {
                begin = ConstraintBegin()
            }
        }
        applyConstraintSet.clone(constraintLayout)
        return begin!!
    }

    /**
     * 带动画的修改
     * @return
     */
    fun beginWithAnim(): ConstraintBegin {
        TransitionManager.beginDelayedTransition(constraintLayout)
        return begin()
    }

    /**
     * 重置
     */
    fun reSet() {
        resetConstraintSet.applyTo(constraintLayout)
    }

    /**
     * 带动画的重置
     */
    fun reSetWidthAnim() {
        TransitionManager.beginDelayedTransition(constraintLayout)
        resetConstraintSet.applyTo(constraintLayout)
    }

    inner class ConstraintBegin {
        /**
         * 清除关系<br></br>
         * 注意：这里不仅仅会清除关系，还会清除对应控件的宽高为 w:0,h:0
         * @param viewIds
         * @return
         */
        fun clear(@IdRes vararg viewIds: Int): ConstraintBegin {
            for (viewId in viewIds) {
                applyConstraintSet.clear(viewId)
            }
            return this
        }

        /**
         * 清除某个控件的，某个关系
         * @param viewId
         * @param anchor
         * @return
         */
        fun clear(viewId: Int, anchor: Int): ConstraintBegin {
            applyConstraintSet.clear(viewId, anchor)
            return this
        }

        /**
         * 为某个控件设置 margin
         * @param viewId 某个控件ID
         * @param left marginLeft
         * @param top   marginTop
         * @param right marginRight
         * @param bottom marginBottom
         * @return
         */
        fun setMargin(
            @IdRes viewId: Int,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int
        ): ConstraintBegin {
            setMarginLeft(viewId, left)
            setMarginTop(viewId, top)
            setMarginRight(viewId, right)
            setMarginBottom(viewId, bottom)
            return this
        }

        /**
         * 为某个控件设置 marginLeft
         * @param viewId 某个控件ID
         * @param left marginLeft
         * @return
         */
        fun setMarginLeft(@IdRes viewId: Int, left: Int): ConstraintBegin {
            applyConstraintSet.setMargin(viewId, ConstraintSet.LEFT, left)
            return this
        }

        /**
         * 为某个控件设置 marginRight
         * @param viewId 某个控件ID
         * @param right marginRight
         * @return
         */
        fun setMarginRight(@IdRes viewId: Int, right: Int): ConstraintBegin {
            applyConstraintSet.setMargin(viewId, ConstraintSet.RIGHT, right)
            return this
        }

        /**
         * 为某个控件设置 marginTop
         * @param viewId 某个控件ID
         * @param top marginTop
         * @return
         */
        fun setMarginTop(@IdRes viewId: Int, top: Int): ConstraintBegin {
            applyConstraintSet.setMargin(viewId, ConstraintSet.TOP, top)
            return this
        }

        /**
         * 为某个控件设置marginBottom
         * @param viewId 某个控件ID
         * @param bottom marginBottom
         * @return
         */
        fun setMarginBottom(@IdRes viewId: Int, bottom: Int): ConstraintBegin {
            applyConstraintSet.setMargin(viewId, ConstraintSet.BOTTOM, bottom)
            return this
        }

        /**
         * 为某个控件设置关联关系 left_to_left_of
         * @param startId
         * @param endId
         * @return
         */
        fun Left_toLeftOf(@IdRes startId: Int, @IdRes endId: Int): ConstraintBegin {
            applyConstraintSet.connect(startId, ConstraintSet.LEFT, endId, ConstraintSet.LEFT)
            return this
        }

        /**
         * 为某个控件设置关联关系 start_to_start_of
         * @param startId
         * @param endId
         * @return
         */
        fun Start_toStartOf(@IdRes startId: Int, @IdRes endId: Int): ConstraintBegin {
            applyConstraintSet.connect(startId, ConstraintSet.START, endId, ConstraintSet.START)
            return this
        }

        /**
         * 为某个控件设置关联关系 left_to_right_of
         * @param startId
         * @param endId
         * @return
         */
        fun Left_toRightOf(@IdRes startId: Int, @IdRes endId: Int): ConstraintBegin {
            applyConstraintSet.connect(startId, ConstraintSet.LEFT, endId, ConstraintSet.RIGHT)
            return this
        }

        /**
         * 为某个控件设置关联关系 start_to_end_of
         * @param startId
         * @param endId
         * @return
         */
        fun Start_toEndOf(@IdRes startId: Int, @IdRes endId: Int): ConstraintBegin {
            applyConstraintSet.connect(startId, ConstraintSet.START, endId, ConstraintSet.END)
            return this
        }

        /**
         * 为某个控件设置关联关系 top_to_top_of
         * @param startId
         * @param endId
         * @return
         */
        fun Top_toTopOf(@IdRes startId: Int, @IdRes endId: Int): ConstraintBegin {
            applyConstraintSet.connect(startId, ConstraintSet.TOP, endId, ConstraintSet.TOP)
            return this
        }

        /**
         * 为某个控件设置关联关系 top_to_bottom_of
         * @param startId
         * @param endId
         * @return
         */
        fun Top_toBottomOf(@IdRes startId: Int, @IdRes endId: Int): ConstraintBegin {
            applyConstraintSet.connect(startId, ConstraintSet.TOP, endId, ConstraintSet.BOTTOM)
            return this
        }

        /**
         * 为某个控件设置关联关系 right_to_left_of
         * @param startId
         * @param endId
         * @return
         */
        fun Right_toLeftOf(@IdRes startId: Int, @IdRes endId: Int): ConstraintBegin {
            applyConstraintSet.connect(startId, ConstraintSet.RIGHT, endId, ConstraintSet.LEFT)
            return this
        }

        /**
         * 为某个控件设置关联关系 end_to_start_of
         * @param startId
         * @param endId
         * @return
         */
        fun End_toStartOf(@IdRes startId: Int, @IdRes endId: Int): ConstraintBegin {
            applyConstraintSet.connect(startId, ConstraintSet.END, endId, ConstraintSet.START)
            return this
        }

        /**
         * 为某个控件设置关联关系 right_to_right_of
         * @param startId
         * @param endId
         * @return
         */
        fun Right_toRightOf(@IdRes startId: Int, @IdRes endId: Int): ConstraintBegin {
            applyConstraintSet.connect(startId, ConstraintSet.RIGHT, endId, ConstraintSet.RIGHT)
            return this
        }

        /**
         * 为某个控件设置关联关系 end_to_end_of
         * @param startId
         * @param endId
         * @return
         */
        fun End_toEndOf(@IdRes startId: Int, @IdRes endId: Int): ConstraintBegin {
            applyConstraintSet.connect(startId, ConstraintSet.END, endId, ConstraintSet.END)
            return this
        }


        /**
         * 为某个控件设置关联关系 bottom_to_bottom_of
         * @param startId
         * @param endId
         * @return
         */
        fun Bottom_toBottomOf(@IdRes startId: Int, @IdRes endId: Int): ConstraintBegin {
            applyConstraintSet.connect(startId, ConstraintSet.BOTTOM, endId, ConstraintSet.BOTTOM)
            return this
        }

        /**
         * 为某个控件设置关联关系 bottom_to_top_of
         * @param startId
         * @param endId
         * @return
         */
        fun Bottom_toTopOf(@IdRes startId: Int, @IdRes endId: Int): ConstraintBegin {
            applyConstraintSet.connect(startId, ConstraintSet.BOTTOM, endId, ConstraintSet.TOP)
            return this
        }

        /**
         * 为某个控件设置宽度
         * @param viewId
         * @param width
         * @return
         */
        fun setWidth(@IdRes viewId: Int, width: Int): ConstraintBegin {
            applyConstraintSet.constrainWidth(viewId, width)
            return this
        }

        /**
         * 某个控件设置高度
         * @param viewId
         * @param height
         * @return
         */
        fun setHeight(@IdRes viewId: Int, height: Int): ConstraintBegin {
            applyConstraintSet.constrainHeight(viewId, height)
            return this
        }

        /**
         * 某个控件百分比高度
         * @param viewId
         * @param percent
         * @return
         */
        fun constrainPercentHeight(@IdRes viewId: Int, percent: Float): ConstraintBegin {
            applyConstraintSet.constrainPercentHeight(viewId, percent)
            return this
        }

        /**
         * 提交应用生效
         */
        fun commit() {
            applyConstraintSet.applyTo(constraintLayout)
        }
    }

    init {
        resetConstraintSet.clone(constraintLayout)
    }
}