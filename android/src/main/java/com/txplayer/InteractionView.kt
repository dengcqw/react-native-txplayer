package com.txplayer

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.txplayer.databinding.ViewInteractiveBinding
import java.lang.ref.WeakReference
import kotlin.Exception


interface InteractiveViewInterface {
    fun submitInteraction(data: String): Unit
}

class InteractiveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // 如果不用代码生成，需添加此工厂
        .build()

    var entity: InteractionEntity? = null

    var lastPosition: Int = 0

    var delegate: WeakReference<InteractiveViewInterface>? = null

    private val binding: ViewInteractiveBinding =
        ViewInteractiveBinding.inflate(LayoutInflater.from(context), this)

    var fullscreen: Boolean = false
        set(value) {
            field = value
            binding.selectPanel.visibility = if (value && entity != null) View.VISIBLE  else View.INVISIBLE
        }

    init {
        binding.selectPanel.submit = { value ->
            var jsonAdapter = moshi.adapter(InteractionSubmitEntity::class.java)
            delegate?.get()?.submitInteraction(jsonAdapter.toJson(value))
        }

        binding.inputPanel.submit = { value ->
            var jsonAdapter = moshi.adapter(InteractionSubmitEntity::class.java)
            delegate?.get()?.submitInteraction(jsonAdapter.toJson(value))
        }
        binding.audioPanel.root.visibility = View.GONE
        binding.audioPanel.tvNext.setOnClickListener {
            var jsonAdapter = moshi.adapter(InteractionSubmitEntity::class.java)
            val value = InteractionSubmitEntity(
                entity?.interactionId,
                entity?.interactionType,
                null,
                null
            )
            delegate?.get()?.submitInteraction(jsonAdapter.toJson(value))
        }
        binding.audioPanel.icPlay.setOnClickListener {
            var jsonAdapter = moshi.adapter(InteractionSubmitEntity::class.java)
            val value = InteractionSubmitEntity(
                entity?.interactionId,
                entity?.interactionType,
                "togglePlay",
                null
            )
            delegate?.get()?.submitInteraction(jsonAdapter.toJson(value))
        }
    }

    fun updateVideoSize(width: Int, height: Int, viewW: Int, viewH: Int) {
        Log.d("InteractionView", "updateVideoSize: w=$width h=$height")
        Log.d("InteractionView", "updateVideoSize: selfw=${viewW} selfh=${viewH}")
        Log.d("InteractionView", "updateVideoSize: ${entity?.vW} selfh=${entity?.vH}")
        binding.areaLayout.updateVideoSize(entity?.vW ?: width, entity?.vH ?: height, viewW, viewH)
    }

    fun setInteraction(jsonString: String) {
        if (jsonString == "") {
            binding.areaLayout.areas = null
            entity = null
            visibility = View.INVISIBLE
            return
        }

        val jsonAdapter = moshi.adapter(InteractionEntity::class.java)
        try {
            entity = jsonAdapter.fromJson(jsonString)
            visibility = View.VISIBLE
            binding.areaLayout.areas = entity?.areaLayouts
            val themeColor = Color.parseColor(entity!!.themeColor)
            binding.areaLayout.themeColor = themeColor

            binding.selectPanel.visibility = View.INVISIBLE
            binding.inputPanel.visibility = View.INVISIBLE
            binding.audioPanel.root.visibility = View.INVISIBLE

            if (entity?.interactionType == 4) {
                binding.audioPanel.root.visibility = View.VISIBLE
                binding.audioPanel.tvInteractionTitle.text = entity?.actionTxt
                binding.audioPanel.tvNext.text = entity?.nextTxt
                binding.audioPanel.tvNext.setTextColor(themeColor)
                binding.audioPanel.icPlay.setColorFilter(themeColor, PorterDuff.Mode.SRC_IN);
            } else if (entity?.interactionType == 1) {
                binding.inputPanel.entity = entity
                binding.inputPanel.themeColor = themeColor
                binding.inputPanel.update()
                binding.inputPanel.visibility = View.VISIBLE
            } else {
                binding.selectPanel.visibility = View.VISIBLE
                binding.selectPanel.entity = entity
                binding.selectPanel.themeColor = themeColor
                binding.selectPanel.update()
            }

            if (!fullscreen) {
                binding.selectPanel.visibility = View.INVISIBLE
                binding.inputPanel.visibility = View.INVISIBLE
                binding.audioPanel.root.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateAnswer(jsonString: String) {
        if (jsonString == "") {
            binding.selectPanel.updateAnswer(null)
            return
        }

        val jsonAdapter = moshi.adapter(InteractionAnswerEntity::class.java)
        try {
            if (entity?.interactionType == 4) {
                jsonAdapter.fromJson(jsonString)?.let { obj ->
                    binding.audioPanel.tvAudioTime.text = obj.hint
                    binding.audioPanel.tvNext.visibility = if (obj.isCorrect == 1) View.VISIBLE else View.GONE
                    binding.audioPanel.icPlay.setImageResource(if (obj.remainAttempts == 1) R.drawable.ic_pause else R.drawable.ic_play)
                }
            } else if (entity?.interactionType == 1) {
                binding.inputPanel.updateAnswer(jsonAdapter.fromJson(jsonString))
            } else {
                binding.selectPanel.updateAnswer(jsonAdapter.fromJson(jsonString))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}