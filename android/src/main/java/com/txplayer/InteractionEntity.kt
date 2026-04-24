package com.txplayer

data class InteractionEntity(
    val themeColor: String,
    val actionTxt: String?,
    val submitTxt: String?,
    val correctTxt: String?,
    val knownTxt: String?,
    val wrongTxt: String?,
    val nextTxt: String?,
    val inputTxt: String?,
    val vH: Int?,
    val vW: Int?,

    val areaLayouts: List<AreaObj>?,
    /**
     * 下拉选择类型
     */
    val dropdown: Dropdown?,

    /**
     * 交互类型 为：2 时可以选择：0：单选 1 多选
     * 交互类型 为：4 时可以选择：2 文件上传(文件链接) 3：语音合成(语音文本)：
     */
    val interactionAttributes: Int?,
    /**
     * 交互ID
     */
    val interactionId: String?,
    /**
     * 交互类型：0：区域点选 1：输入 2：下拉选择 3：文字指导 4：语音指导
     */
    val interactionType: Int?,
    /**
     * 用户提示
     */
    val prompt: String?,
    /**
     * 文字指导
     */
    val textGuidance: TextGuidance?,
    /**
     * 语音指导
     */
    val voiceGuidance: VoiceGuidance?
)

data class AreaObj(val x: Float, val y: Float, val w: Float, val h: Float)

data class Dropdown(val options: List<Option>)

data class Option(
    /**
     * 是否是正确答案：0 否 1 是
     */
    val isAnswer: Int?,
    /**
     * 选项内容
     */
    val value: String?,
    val uniqueId: String?
)

/**
 * 语音指导
 */
data class VoiceGuidance(val fileUrl: String?)

data class TextGuidance(val content: String?)


// submit Entity
data class InteractionSubmitEntity(
    val interactionId: String?,
    val interactionType: Int?,
    val input: String?,
    val selected: List<Int>?
)

// Answer result Entity
data class InteractionAnswerEntity(
    val isPractice: Int,
    val hint: String?,
    val correctAnswer: String?,
    /**
     * 是否正确：0-错误 1-正确
     */
    val isCorrect: Int,
    /**
     * 剩余作答次数
     */
    val remainAttempts: Int
)
