package com.txplayer

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.children
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.txplayer.databinding.ViewInteractiveOptionBinding
import com.txplayer.databinding.ViewInteractiveSelectBinding
import kotlin.Exception


class InteractiveSelectView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var entity: InteractionEntity? = null

    var submit: ((value: InteractionSubmitEntity) -> Unit)? = null

    private val binding: ViewInteractiveSelectBinding =
        ViewInteractiveSelectBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.llSubmit.setOnClickListener {
            entity?.let { it ->
                submit?.invoke(InteractionSubmitEntity(
                    it.interactionId,
                    it.interactionType,
                    null,
                    selected
                ))
            }
        }
    }

    val charList = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
    val selected: MutableList<Int> = mutableListOf()
    val optionBindings: MutableList<ViewInteractiveOptionBinding> = mutableListOf()

    fun getTitleIcon(type: Int?): Int {
        if (type == 1) return R.drawable.ic_edit
        if (type == 3 || type == 4) return R.drawable.ic_warning
        if (type == 2) return R.drawable.ic_title_correct
        return R.drawable.ic_hand_touch
    }

    fun updateSubmit(type: Int?) {
        if (type == 0 || type == 2) {
            binding.llSubmit.isEnabled = false
            binding.llSubmit.visibility = View.VISIBLE
        } else if (type == 3) {
            binding.llSubmit.isEnabled = true
            binding.llSubmit.visibility = View.VISIBLE
        } else if (type == 4) { // 语音播放
            binding.llSubmit.visibility = View.INVISIBLE
        }
    }


    fun update() {
        // 输入
        if (entity?.interactionType == 1) {
            return
        }

        selected.clear()
        optionBindings.clear()
        binding.llOptionCon.removeAllViews()

        entity?.apply {
            updateSubmit(interactionType)
            binding.icTitle.setImageResource(getTitleIcon(interactionType))
            binding.tvInteractionTitle.text = actionTxt
            binding.tvPrompt.text = prompt
            binding.tvSubmit.text = if (interactionType == 3) knownTxt else submitTxt
            dropdown?.options?.forEachIndexed { index, option ->
                val optionBinding = ViewInteractiveOptionBinding.inflate(LayoutInflater.from(context), binding.llOptionCon, true)
                if (option.value.isNullOrEmpty()) {
                    optionBinding.tvTitle.text = charList[index]
                } else {
                    optionBinding.tvTitle.text = charList[index] + "、" + option.value
                }
                optionBinding.root.setOnClickListener {
                    binding.llSubmit.isEnabled = true
                    if (interactionAttributes == 0) { // 单选
                        optionBindings.forEach { binding ->
                            binding.icSelect.setImageResource(R.drawable.ic_unselect)
                        }
                        selected.clear()
                    }
                    if (selected.contains(index)) {
                        optionBinding.icSelect.setImageResource(R.drawable.ic_unselect)
                        selected.remove(index)
                    } else {
                        optionBinding.icSelect.setImageResource(R.drawable.ic_select)
                        selected.add(index)
                    }
                }
                optionBindings.add(optionBinding)
            }
        }
    }

    fun updateAnswer(answer: InteractionAnswerEntity?) {
        if (answer == null || entity == null) {
            binding.llResult.visibility = View.GONE
            return
        }
        binding.llResult.visibility = View.VISIBLE
        binding.icResult.setImageResource(if (answer.isCorrect == 1) R.drawable.ic_correct else R.drawable.ic_wrong)
        binding.tvResultStatus.text = if (answer.isCorrect == 1) entity!!.correctTxt else entity!!.wrongTxt
        binding.tvResultStatus.setTextColor(
            if (answer.isCorrect == 1)
                context.getColor(R.color.interaction_correct)
            else
                context.getColor(R.color.interaction_wrong)
        )
        binding.tvResultText.text = answer.hint
    }
}