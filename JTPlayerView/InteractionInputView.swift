//
//  InteractionView.swift
//  Pods
//
//  Created by 邓锦龙 on 2026/3/23.
//

import UIKit
import SnapKit


public class InteractionInputView: UIView {
    let attributes: [NSAttributedString.Key: Any] = [
        .foregroundColor: UIColor(hex: "#E1E1E1"),  // 占位符颜色
        .font: UIFont.systemFont(ofSize: 14)        // 占位符字体大小
    ]
    // MARK: - UI
    private let resultView = ResultView()
    private let titleView = TitleView()
    private let promteLabel = UILabel()
    private let scrollView = UIScrollView()
    private let submitBtn = UIButton()
    private let textField = UITextField()
    private let stackView = UIStackView()
    var entity: InteractionEntity?

    
    @objc public func submitAction() {
        let it = InteractionSubmitEntity(interactionId: entity?.interactionId, interactionType: entity?.interactionType.rawValue, input: textField.text, selected: nil )
        do {
            let encoder = JSONEncoder()
            let jsonData = try encoder.encode(it)
            let jsonString = String(data: jsonData, encoding: .utf8)!
            NotificationCenter.default.post(name: NSNotification.Name.init(rawValue: "com.jt.sand.interaction.submit"), object: jsonString)
        } catch {
            print("编码失败: \(error)")
        }
    }
    
    func showInteraction(data: InteractionEntity) {
        if (data.interactionType != .input) {
            isHidden = true
            entity = nil
            return
        }
        entity = data
        isHidden = false
        submitBtn.isEnabled = false
        resultView.isHidden = true
        textField.text = ""
        let attributes: [NSAttributedString.Key: Any] = [
            .foregroundColor: UIColor.white.withAlphaComponent(0.6)
        ]
        let attributedText = NSAttributedString(string: data.inputTxt, attributes: attributes)
        textField.attributedPlaceholder = attributedText
        resultView.statusError = data.wrongTxt
        resultView.statusCorrect = data.correctTxt

        titleView.titleIcon.image = SourceHelper.image(with: "ic_edit")?.withTintColor( UIColor(hex: data.themeColor))
        titleView.titleLabel.text = data.actionTxt
        promteLabel.text = data.prompt
        if #available(iOS 15.0, *) {
            submitBtn.configuration?.background.backgroundColor = UIColor(hex: data.themeColor)
        } else {
            // Fallback on earlier versions
        }
        submitBtn.isEnabled = false
        submitBtn.setAttributedTitle(getAttrStr(data.submitTxt, UIColor.white), for: UIControl.State.normal)
        submitBtn.setAttributedTitle(getAttrStr(data.submitTxt, UIColor.gray), for: UIControl.State.disabled)
    }
    
    func updateAnswer(answer: InteractionAnswerEntity?) {
        guard let answer = answer else {
            resultView.isHidden = true
            return
        }
        resultView.isHidden = false
        resultView.update(answer)
        if (answer.isCorrect == 0 && answer.isPractice == 1 && answer.remainAttempts == 0) {
            submitBtn.isEnabled = true
            textField.text = answer.correctAnswer
        } else if (answer.isCorrect == 0 && answer.remainAttempts > 0) {
            submitBtn.isEnabled = true
        } else {
            submitBtn.isEnabled = false
            endEditing(true)
        }
    }

    // MARK: - Init
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUI()
        resultView.isHidden = true
        setupLayout()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupUI()
        setupLayout()
    }

    
    // MARK: - Setup
    
    private func setupUI() {
        backgroundColor = UIColor.black
        
        // container
        addSubview(stackView)
        stackView.addArrangedSubview(titleView)
        stackView.addArrangedSubview(resultView)
        addSubview(scrollView)
        scrollView.addSubview(promteLabel)
        addSubview(submitBtn)
        addSubview(textField)
        // textField
        let color = UIColor(red: 49/255, green: 49/255, blue: 51/255, alpha: 1.0)
        textField.backgroundColor = color
        textField.font = UIFont.systemFont(ofSize: 14)
        textField.layer.cornerRadius = 15
        textField.textColor = UIColor.white
        let paddingView = UIView(frame: CGRect(x: 0, y: 0, width: 15, height: 30))
        textField.leftView = paddingView
        textField.leftViewMode = .always
        textField.addTarget(self, action: #selector(textFieldDidChange(_:)), for: .editingChanged)
        
        promteLabel.textColor = UIColor.white
        promteLabel.font = UIFont.boldSystemFont(ofSize: 14)
        promteLabel.numberOfLines = 0
        promteLabel.text = ""

        // submit
        submitBtn.layer.cornerRadius = 15
        submitBtn.clipsToBounds = true
        submitBtn.addTarget(self, action: #selector(submitAction), for: UIControl.Event.touchUpInside)
        if #available(iOS 15.0, *) {
            var config = UIButton.Configuration.filled()
            config.title = "按钮"
            config.contentInsets = NSDirectionalEdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16)
            config.background.backgroundColor = UIColor(red: 0.953, green: 0.608, blue: 0, alpha: 1)
            submitBtn.configuration = config
        } else {
            // Fallback on earlier versions
        }
    }
    
    @objc private func textFieldDidChange(_ textField: UITextField) {
        let isTextValid = !(textField.text?.trimmingCharacters(in: .whitespaces).isEmpty ?? true)
        submitBtn.isEnabled = isTextValid
    }
    
    private func setupLayout() {
        stackView.snp.makeConstraints {
            $0.top.equalToSuperview().offset(5)
            $0.left.right.equalToSuperview().inset(16)
            $0.height.equalTo(30)
        }
        textField.snp.makeConstraints {
            $0.top.equalTo(scrollView.snp.bottom).offset(10)
            $0.bottom.equalToSuperview().inset(20)
            $0.height.equalTo(30)
            $0.left.equalToSuperview().inset(16)
        }
        submitBtn.snp.makeConstraints {
            $0.centerY.equalTo(textField.snp.centerY)
            $0.left.equalTo(textField.snp.right).offset(12)
            $0.bottom.equalToSuperview().inset(20)
            $0.right.equalToSuperview().inset(16)
            $0.height.equalTo(30)
        }
        textField.setContentHuggingPriority(.defaultLow, for: .horizontal)
        textField.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        submitBtn.setContentHuggingPriority(.required, for: .horizontal)
        submitBtn.setContentCompressionResistancePriority(.required, for: .horizontal)
        
        scrollView.snp.makeConstraints {
            $0.height.lessThanOrEqualTo(80)
            $0.height.greaterThanOrEqualTo(30)
            $0.top.equalTo(stackView.snp.bottom).offset(3)
            $0.bottom.equalTo(textField.snp.top).offset(-5)
            $0.left.right.equalToSuperview().inset(16)
        }
        promteLabel.snp.makeConstraints {
            $0.left.top.bottom.equalToSuperview()
            $0.width.equalTo(stackView.snp.width)
        }
        promteLabel.setContentHuggingPriority(.required, for: .horizontal)
        promteLabel.setContentCompressionResistancePriority(.required, for: .horizontal)
    }
}
