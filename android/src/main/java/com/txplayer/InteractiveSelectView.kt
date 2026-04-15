package com.txplayer

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.txplayer.databinding.ViewInteractiveOptionBinding
import com.txplayer.databinding.ViewInteractiveSelectBinding
import androidx.core.graphics.ColorUtils

class InteractiveSelectView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var entity: InteractionEntity? = null
    var themeColor: Int? = null
    var submit: ((value: InteractionSubmitEntity) -> Unit)? = null

    private val binding: ViewInteractiveSelectBinding =
        ViewInteractiveSelectBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.llSubmit.setOnClickListener {
            entity?.let { it ->
                binding.llSubmit.isEnabled = false
                updateSubmitBg()
                submit?.invoke(InteractionSubmitEntity(
                    it.interactionId,
                    it.interactionType,
                    null,
                    selected
                ))
            }
        }
    }

    val charList = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O")
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
            updateSubmitBg()
        } else if (type == 3) {
            binding.llSubmit.isEnabled = true
            binding.llSubmit.visibility = View.VISIBLE
            updateSubmitBg()
        } else if (type == 4) { // 语音播放
            binding.llSubmit.visibility = View.INVISIBLE
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

    fun update() {
        // 输入
        if (entity?.interactionType == 1) {
            return
        }

        binding.llResult.visibility = GONE
        selected.clear()
        optionBindings.clear()
        binding.llOptionCon.removeAllViews()

        entity?.apply {
            updateSubmit(interactionType)
            binding.icTitle.setImageResource(getTitleIcon(interactionType))
            this@InteractiveSelectView.themeColor?.let { color ->
                binding.icTitle.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            binding.tvInteractionTitle.text = actionTxt
            if (interactionType == 3) {
                binding.tvPrompt.text = textGuidance?.content
                binding.tvSubmit.text = knownTxt
            } else {
                binding.tvPrompt.text = prompt
                binding.tvSubmit.text = submitTxt
                dropdown?.options?.forEachIndexed { index, option ->
                    val optionBinding = ViewInteractiveOptionBinding.inflate(LayoutInflater.from(context), binding.llOptionCon, true)
                    if (option.value.isNullOrEmpty()) {
                        optionBinding.tvTitle.text = charList[index]
                    } else {
                        optionBinding.tvTitle.text = charList[index] + "、" + option.value
                    }
                    optionBinding.root.setOnClickListener {
                        binding.llSubmit.isEnabled = true
                        updateSubmitBg()
                        if (interactionAttributes == 0 || interactionType == 0) { // 单选
                            if (selected.isNotEmpty()) {
                                if (index == selected[0]) {
                                    return@setOnClickListener
                                }
                                optionBindings[selected[0]].let{ binding ->
                                    binding.icSelect.clearColorFilter()
                                    binding.icSelect.setImageResource(R.drawable.ic_unselect)
                                }
                            }
                            selected.clear()

                            optionBinding.icSelect.setColorFilter(this@InteractiveSelectView.themeColor!!, PorterDuff.Mode.SRC_IN);
                            optionBinding.icSelect.setImageResource(R.drawable.ic_select)
                            selected.add(index)
                        } else {
                            if (selected.contains(index)) {
                                optionBinding.icSelect.clearColorFilter()
                                optionBinding.icSelect.setImageResource(R.drawable.ic_unselect)
                                selected.remove(index)
                            } else {
                                optionBinding.icSelect.setColorFilter(this@InteractiveSelectView.themeColor!!, PorterDuff.Mode.SRC_IN);
                                optionBinding.icSelect.setImageResource(R.drawable.ic_select)
                                selected.add(index)
                            }
                        }
                    }
                    optionBindings.add(optionBinding)
                }
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
        highlightAnswer(false)
        if (answer.isCorrect == 0 && answer.isPractice == 1 && answer.remainAttempts == 0) {
            binding.llSubmit.isEnabled = true
            updateSubmitBg()
            highlightAnswer(true, answer.hint)
        } else if (answer.isCorrect == 0 && answer.remainAttempts > 0) {
            binding.llSubmit.isEnabled = true
            updateSubmitBg()
        }
    }

    fun highlightAnswer(show: Boolean, hint: String? = null) {
        val borderColor = context.getColor(R.color.interaction_correct)
        val borderWidth = 1

        entity?.dropdown?.options?.forEachIndexed { index, option ->
            if (option.isAnswer == 1) {
                optionBindings[index].let { optionBinding ->
                    if (show) {
                        val rootView = optionBinding.root
                        val scale = resources.displayMetrics.density
                        val borderWidthPx = (borderWidth * scale + 0.5f).toInt()

                        val backgroundDrawable = android.graphics.drawable.GradientDrawable()
                        backgroundDrawable.setStroke(borderWidthPx, borderColor)
                        backgroundDrawable.cornerRadius = 0f

                        rootView.background = backgroundDrawable
                        if (hint != null) {
                            binding.tvResultText.text = hint.replace("$1", charList[index])
                        }
                    } else {
                        rootView.background = null
                    }
                }

            }
        }
    }
}
