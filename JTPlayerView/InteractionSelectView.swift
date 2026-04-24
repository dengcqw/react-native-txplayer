//
//  InteractionView.swift
//  Pods
//
//  Created by 邓锦龙 on 2026/3/23.
//

import UIKit
import SnapKit

public class InteractionSelectView: UIView {
    
    // MARK: - UI
    private let resultView = ResultView()
    private let submitBtn = UIButton()
    private let titleView = TitleView()

    
    private let promptLabel = UILabel()
    
    private let scrollView = UIScrollView()
    private let stackView = UIStackView()
    
    let charList = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O"]
    var selected: [Int] = []
    var entity: InteractionEntity?
    var options: [OptionView] = []
    var multiSelect = false
    var themeColor: UIColor {
        get {
            guard let color = entity?.themeColor else {
                return UIColor(red: 0.953, green: 0.608, blue: 0, alpha: 1)
            }
            return UIColor(hex: color)
        }
    }

    @objc public func submitAction() {
        let it = InteractionSubmitEntity(interactionId: entity?.interactionId, interactionType: entity?.interactionType.rawValue, input: nil, selected: selected )
        do {
            let encoder = JSONEncoder()
            let jsonData = try encoder.encode(it)
            let jsonString = String(data: jsonData, encoding: .utf8)!
            NotificationCenter.default.post(name: NSNotification.Name.init(rawValue: "com.jt.sand.interaction.submit"), object: jsonString)
            submitBtn.isEnabled = false
        } catch {
            print("编码失败: \(error)")
        }
    }

    func titleIcon(type: InteractionType) -> String {
        if (type == .dropdown) { return "ic_title_correct" }
        if (type == .textGuidance) { return "ic_warning" }
        return "ic_hand_touch"
    }

    
    func updateSubmit(data: InteractionEntity) {
        if #available(iOS 15.0, *) {
            submitBtn.configuration?.background.backgroundColor = UIColor(hex: data.themeColor)
        } else {
            // Fallback on earlier versions
        }
        if (data.interactionType == .textGuidance) {
            submitBtn.isEnabled = true
            submitBtn.setAttributedTitle(getAttrStr(data.knownTxt, UIColor.white), for: UIControl.State.normal)
            submitBtn.setAttributedTitle(getAttrStr(data.knownTxt, UIColor.gray), for: UIControl.State.disabled)
        } else {
            submitBtn.isEnabled = false
            submitBtn.setAttributedTitle(getAttrStr(data.submitTxt, UIColor.white), for: UIControl.State.normal)
            submitBtn.setAttributedTitle(getAttrStr(data.submitTxt, UIColor.gray), for: UIControl.State.disabled)
        }
    }
    
    @objc func boxTapped(_ gesture: UITapGestureRecognizer) {
        if let view = gesture.view as? OptionView {
            submitBtn.isEnabled = true
            
            if multiSelect {
                if (selected.contains(view.index)) {
                    selected.remove(at: selected.firstIndex(of: view.index)!)
                    view.selected = false
                    view.update()
                } else {
                    selected.append(view.index)
                    view.selected = true
                    view.update()
                }
            } else {
                selected.forEach { index in
                    options[index].selected = false
                    options[index].update()
                }
                selected.removeAll()
                selected.append(view.index)
                view.selected = true
                view.update()
            }
        }
    }
    
    func showInteraction(data: InteractionEntity) {
        if (data.interactionType == .area || data.interactionType == .dropdown || data.interactionType == .textGuidance) {
            entity = data
            multiSelect = data.interactionType != .textGuidance && data.interactionAttributes == 1
            selected.removeAll()
            options.forEach { view in
                stackView.removeArrangedSubview(view)
                view.removeFromSuperview()
            }
            options.removeAll()
            resultView.statusError = data.wrongTxt
            resultView.statusCorrect = data.correctTxt
            
            updateSubmit(data: data)
            promptLabel.text = data.prompt
            titleView.titleLabel.text = data.actionTxt
            titleView.titleIcon.image = SourceHelper.image(with: titleIcon(type: data.interactionType))?.withTintColor(UIColor(hex: data.themeColor))
            if (data.interactionType == .textGuidance) {
                promptLabel.text = data.textGuidance?.content
            } else if let dropdown = data.dropdown {
                for (index, element) in dropdown.options.enumerated() {
                    let aView = OptionView()
                    aView.index = index
                    aView.themeColor = themeColor
                    aView.single = !multiSelect
                    if (element.value == nil || element.value!.isEmpty) {
                        aView.text.text = charList[index]
                    } else {
                        aView.text.text = charList[index] + "、" + (element.value ?? "")
                    }
                    
                    let tap = UITapGestureRecognizer(target: self, action: #selector(boxTapped(_:)))
                    isUserInteractionEnabled = true
                    aView.addGestureRecognizer(tap)
//                    aView.heightAnchor.constraint(equalToConstant: 30).isActive = true
                    options.append(aView)
                    stackView.addArrangedSubview(aView)
                }
            }
            
            isHidden = false
        } else {
            entity = nil
            isHidden = true
        }
    }
    
    func updateAnswer(answer: InteractionAnswerEntity?) {
        guard let answer = answer else {
            resultView.isHidden = true
            return
        }
        resultView.isHidden = false
        resultView.update(answer)
        highlightAnswer(false)
        if (answer.isCorrect == 0 && answer.isPractice == 1 && answer.remainAttempts == 0) {
            submitBtn.isEnabled = true
            highlightAnswer(true)
        } else if (answer.isCorrect == 0 && answer.remainAttempts > 0) {
            submitBtn.isEnabled = true
        }
    }
    
    func highlightAnswer(_ show: Bool) {
        var answer: [String] = []
        if let dropdown = entity?.dropdown {
            for (index, element) in dropdown.options.enumerated() {
                if (element.isAnswer == 1) {
                    options[index].showBorder(show)
                    answer.append(charList[index])
                }
            }
        }
        if (entity?.interactionType == .area) {
            resultView.updateHint(answer.joined(separator: ","))
        }
    }

    // MARK: - Init
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUI()
        setupLayout()
        resultView.isHidden = true
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupUI()
        setupLayout()
    }
    
    
    // MARK: - Setup
    
    private func setupUI() {
        backgroundColor = UIColor.black.withAlphaComponent(0.5)
        
        // container
        addSubview(titleView)
        addSubview(scrollView)
        scrollView.addSubview(promptLabel)
        scrollView.addSubview(stackView)
        addSubview(resultView)
        addSubview(submitBtn)

        // submit
        submitBtn.layer.cornerRadius = 15
        submitBtn.clipsToBounds = true
        submitBtn.addTarget(self, action: #selector(submitAction), for: UIControl.Event.touchUpInside)
        if #available(iOS 15.0, *) {
            var config = UIButton.Configuration.filled()
            config.title = "按钮"
            config.contentInsets = NSDirectionalEdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16)
            submitBtn.configuration = config
        } else {
            // Fallback on earlier versions
        }

        
        // prompt
        promptLabel.textColor = UIColor(white: 0.88, alpha: 1)
        promptLabel.font = .boldSystemFont(ofSize: 14)
        promptLabel.numberOfLines = 0
        promptLabel.text = "请选择正确位置"
        
        stackView.axis = .vertical          // 垂直布局，类似 LinearLayout.VERTICAL
        stackView.alignment = .leading         // 子视图宽度填充父视图
        stackView.distribution = .fill  // 子视图等高
        stackView.spacing = 8
    }
    
    private func setupLayout() {
        resultView.snp.makeConstraints {
            $0.bottom.equalToSuperview().offset(-60)
            $0.height.equalTo(30)
            $0.left.equalToSuperview().inset(16)
        }
        
        submitBtn.snp.makeConstraints {
            $0.right.equalToSuperview().inset(16)
            $0.height.equalTo(30)
            $0.bottom.equalToSuperview().inset(20)
        }

        titleView.snp.makeConstraints {
            $0.top.equalToSuperview().offset(25)
            $0.height.equalTo(20)
            $0.left.right.equalToSuperview().inset(16)
        }

        scrollView.snp.makeConstraints {
            $0.top.equalTo(titleView.snp.bottom).offset(10)
            $0.bottom.equalTo(resultView.snp.top).offset(10)
            $0.left.right.equalToSuperview().inset(16)
        }
        
        promptLabel.snp.makeConstraints {
            $0.top.left.right.equalToSuperview()
        }
        stackView.snp.makeConstraints {
            $0.top.equalTo(promptLabel.snp.bottom).offset(12)
            $0.left.right.bottom.equalToSuperview()
            $0.width.equalToSuperview()
        }
    }
    
}

