//
//  Entity.swift
//  Pods
//
//  Created by 邓锦龙 on 2026/3/23.
//

import Foundation

enum InteractionType: Int, Codable {
    case area = 0
    case input = 1
    case dropdown = 2
    case textGuidance = 3
    case voiceGuidance = 4
}

// MARK: - InteractionEntity
struct InteractionEntity: Codable {
    let themeColor: String
    let actionTxt: String
    let submitTxt: String
    let correctTxt: String
    let knownTxt: String
    let wrongTxt: String
    let audioTxt: String

    let areaLayouts: [AreaObj]?
    /// 下拉选择类型
    let dropdown: Dropdown?

    /**
     交互类型 为：2 时可以选择：0：单选 1 多选
     交互类型 为：4 时可以选择：2 文件上传(文件链接) 3：语音合成(语音文本)
     */
    let interactionAttributes: Int?

    /// 交互ID
    let interactionId: String?

    /// 交互类型：0：区域点选 1：输入 2：下拉选择 3：文字指导 4：语音指导
    let interactionType: InteractionType

    /// 用户提示
    let prompt: String?

    /// 文字指导
    let textGuidance: TextGuidance?

    /// 语音指导
    let voiceGuidance: VoiceGuidance?
}

// MARK: - AreaObj
struct AreaObj: Codable {
    let x: CGFloat
    let y: CGFloat
    let w: CGFloat
    let h: CGFloat
}

// MARK: - Dropdown
struct Dropdown: Codable {
    let options: [Option]
}

// MARK: - Option
struct Option: Codable {
    /// 是否是正确答案：0 否 1 是
    let isAnswer: Int?

    /// 选项内容
    let value: String?

    let uniqueId: String?
}

// MARK: - VoiceGuidance
struct VoiceGuidance: Codable {
    let fileUrl: String?
}

// MARK: - TextGuidance
struct TextGuidance: Codable {
    let content: String?
}

// MARK: - Submit Entity
struct InteractionSubmitEntity: Codable {
    let interactionId: String?
    let interactionType: Int?
    let input: String?
    let selected: [Int]?
}

// MARK: - Answer result Entity
struct InteractionAnswerEntity: Codable {
    let interactionType: Int
    let hint: String
    let correctAnswer: String?

    /// 是否正确：0-错误 1-正确
    let isCorrect: Int

    /// 剩余作答次数
    let remainAttempts: Int
}
