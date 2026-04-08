package com.txplayer

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.txplayer.databinding.ViewInteractiveInputBinding
import com.txplayer.databinding.ViewInteractiveOptionBinding
import com.txplayer.databinding.ViewInteractiveSelectBinding
import kotlin.Exception


class InteractiveInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var entity: InteractionEntity? = null
    var themeColor: Int? = null
    var submit: ((value: InteractionSubmitEntity) -> Unit)? = null

    private val binding: ViewInteractiveInputBinding =
        ViewInteractiveInputBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.llSubmit.setOnClickListener {
            entity?.let { it ->
                binding.llSubmit.isEnabled = false
                submit?.invoke(InteractionSubmitEntity(
                    it.interactionId,
                    it.interactionType,
                    binding.etInput.text.trim().toString(),
                    null
                ))
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
        binding.llSubmit.isEnabled = true

        themeColor?.let { color ->
            val scale = resources.displayMetrics.density
            val cornerRadius = 15 * scale

            val backgroundDrawable = android.graphics.drawable.GradientDrawable()
            backgroundDrawable.setColor(color)
            backgroundDrawable.cornerRadius = cornerRadius

            binding.llSubmit.background = backgroundDrawable
            binding.icTitle.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

        entity?.apply {
            binding.tvInteractionTitle.text = actionTxt
            binding.etInput.hint = ""
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
            binding.etInput.setText(answer.correctAnswer)
        } else if (answer.isCorrect == 0 && answer.remainAttempts > 0) {
            binding.llSubmit.isEnabled = true
        }
    }
}