package com.txplayer

import android.content.Context
import android.graphics.Rect
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.graphics.ColorUtils
import com.txplayer.databinding.ViewInteractiveInputBinding
import kotlin.math.abs


class InteractiveInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var entity: InteractionEntity? = null
    var themeColor: Int? = null
    var submit: ((value: InteractionSubmitEntity) -> Unit)? = null

    private val binding: ViewInteractiveInputBinding =
        ViewInteractiveInputBinding.inflate(LayoutInflater.from(context), this, true)

    private var usableHeightPrevious = 0

    init {
        binding.llSubmit.setOnClickListener {
            entity?.let { it ->
                binding.llSubmit.isEnabled = false
                updateSubmitBg()
                submit?.invoke(InteractionSubmitEntity(
                    it.interactionId,
                    it.interactionType,
                    binding.etInput.text.trim().toString(),
                    null
                ))
            }
        }

        // 监听键盘弹出，调整布局使输入框跟随键盘弹起
        viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            getWindowVisibleDisplayFrame(rect)
            val usableHeightNow = rect.bottom - rect.top
            if (usableHeightPrevious != usableHeightNow) {
                val usableHeightSansKeyboard = rootView.height
                val heightDifference = abs(usableHeightSansKeyboard - usableHeightNow)

                var layoutParams: LinearLayout.LayoutParams = binding.llCon.layoutParams as LinearLayout.LayoutParams
                if (heightDifference > usableHeightNow / 4) {
                    // 键盘弹出，减少高度让输入框可见
                    layoutParams.bottomMargin = heightDifference
                } else {
                    // 键盘收起，恢复高度
                    layoutParams.bottomMargin = 0
                }
                this@InteractiveInputView.requestLayout()

                usableHeightPrevious = usableHeightNow
            }
        }
    }

    fun update() {
        // 输入
        if (entity?.interactionType != 1) {
            return
        }
        binding.llResult.visibility = GONE
        binding.etInput.setText("")
        binding.etInput.hint = entity?.inputTxt
        binding.llSubmit.isEnabled = true
        updateSubmitBg()

        themeColor?.let { color ->
            binding.icTitle.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

        entity?.apply {
            binding.tvInteractionTitle.text = actionTxt
            binding.tvSubmit.text = submitTxt
            binding.tvPrompt.text = prompt
        }
    }

    fun updateAnswer(answer: InteractionAnswerEntity?) {
        if (answer == null || entity == null) {
            binding.llResult.visibility = GONE
            return
        }
        binding.llResult.visibility = VISIBLE
        binding.icResult.setImageResource(if (answer.isCorrect == 1) R.drawable.ic_correct else R.drawable.ic_wrong)
        binding.tvResultStatus.text = if (answer.isCorrect == 1) entity!!.correctTxt else entity!!.wrongTxt
        binding.tvResultStatus.setTextColor(
            if (answer.isCorrect == 1)
                context.getColor(R.color.interaction_correct)
            else
                context.getColor(R.color.interaction_wrong)
        )
        binding.tvResultText.text = answer.hint
        if (answer.isCorrect == 0 && answer.isPractice == 1 && answer.remainAttempts == 0) {
            binding.llSubmit.isEnabled = true
            updateSubmitBg()
            binding.etInput.setText(answer.correctAnswer)
        } else if (answer.isCorrect == 0 && answer.remainAttempts > 0) {
            binding.llSubmit.isEnabled = true
            updateSubmitBg()
        }
    }

    fun updateSubmitBg() {
        themeColor?.let { color ->
            val scale = resources.displayMetrics.density
            val cornerRadius = 15 * scale

            val backgroundDrawable = android.graphics.drawable.GradientDrawable()
            if (binding.llSubmit.isEnabled) {
                backgroundDrawable.setColor(color)
            } else {
                val newColor = ColorUtils.blendARGB(color, android.graphics.Color.GRAY, 0.6f)
                backgroundDrawable.setColor(newColor)
            }
            backgroundDrawable.cornerRadius = cornerRadius

            binding.llSubmit.background = backgroundDrawable
        }
    }
}