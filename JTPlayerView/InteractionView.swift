//
//  InteractionView.swift
//  Pods
//
//  Created by 邓锦龙 on 2026/3/23.
//

import UIKit
import SnapKit


@objc(JTInteractionView)
public class InteractionView: UIView {
    let selectView = InteractionSelectView()
    let editView = InteractionInputView()
    let audioView = InteractionAudioView()
    let areaView = InteractionAreaView()

    weak var textFieldBottomConstraint: Constraint!
    
    var entity: InteractionEntity?
    
    @objc public var fullscreen = false {
        didSet {
            if (fullscreen && entity != nil) {
                selectView.showInteraction(data: entity!)
                editView.showInteraction(data: entity!)
                audioView.showInteraction(data: entity!)
            } else {
                selectView.isHidden = true
                editView.isHidden = true
                audioView.isHidden = true
            }
        }
    }
    
    @objc public func updateVideoSize(width: Int, height: Int) {
        areaView.updateVideoSize(width: width, height: height)
    }
    @objc public func updateVideoSnapShot(image: UIImage) {
        areaView.updateVideoSnapShot(image: image)
    }

    @objc public func showInteraction(jsonStr: String) {
        if (jsonStr == "") {
            entity = nil
            areaView.areaLayout = nil
            isHidden = true
            return
        }
        
        do {
            let jsonData = jsonStr.data(using: .utf8)!
            let entity = try JSONDecoder().decode(InteractionEntity.self, from: jsonData)
            self.entity = entity
            if let areaLayouts = entity.areaLayouts, areaLayouts.count > 0 {
                areaView.themeColor = UIColor(hex: entity.themeColor)
                areaView.areaLayout = areaLayouts
            } else {
                areaView.areaLayout = []
            }
            if (fullscreen) {
                selectView.showInteraction(data: entity)
                editView.showInteraction(data: entity)
                audioView.showInteraction(data: entity)
            } else {
                selectView.isHidden = true
                editView.isHidden = true
                audioView.isHidden = true
            }
            isHidden = false
        } catch {
            print("解码失败: \(error)")
        }
    }
    
    @objc public func updateAnswer(jsonStr: String) {
        selectView.updateAnswer(answer: nil)
        editView.updateAnswer(answer: nil)
        audioView.updateAnswer(answer: nil)
        if (jsonStr == "") {
            return
        }
        do {
            let jsonData = jsonStr.data(using: .utf8)!
            let ans = try JSONDecoder().decode(InteractionAnswerEntity.self, from: jsonData)
            if (entity?.interactionType == .voiceGuidance) {
                audioView.updateAnswer(answer: ans)
            } else if (entity?.interactionType == .input) {
                editView.updateAnswer(answer: ans)
            } else {
                selectView.updateAnswer(answer: ans)
            }
        } catch {
            print("解码失败: \(error)")
        }
    }
    
    // MARK: - Init
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        addSubview(areaView)
        addSubview(selectView)
        addSubview(editView)
        addSubview(audioView)
        selectView.isHidden = true
        editView.isHidden = true
        audioView.isHidden = true
        setupKeyboardNotifications()
        setupLayout()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    public override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
        return true
    }
    public override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        if (self.isHidden) {
            return nil
        }
        return super.hitTest(point, with: event) ?? self
    }
    
    private func setupLayout() {
        selectView.snp.makeConstraints {
            $0.bottom.equalToSuperview()
            $0.top.equalToSuperview()
            $0.width.equalTo(300)
            $0.right.equalToSuperview()
        }
        
        editView.snp.makeConstraints {
            $0.left.right.equalToSuperview()
            textFieldBottomConstraint = $0.bottom.equalToSuperview().constraint
        }

        audioView.snp.makeConstraints {
            $0.bottom.equalToSuperview().offset(-60)
            $0.height.equalTo(50)
            $0.centerX.equalToSuperview()
        }
        areaView.snp.makeConstraints {
            $0.edges.equalToSuperview()
        }
    }
    
    private func setupKeyboardNotifications() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillShow),
            name: UIResponder.keyboardWillShowNotification,
            object: nil
        )
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillHide),
            name: UIResponder.keyboardWillHideNotification,
            object: nil
        )
    }
    
    @objc private func keyboardWillShow(_ notification: Notification) {
        guard let userInfo = notification.userInfo,
              let keyboardFrame = userInfo[UIResponder.keyboardFrameEndUserInfoKey] as? CGRect,
              let duration = userInfo[UIResponder.keyboardAnimationDurationUserInfoKey] as? TimeInterval
        else { return }
        
        let keyboardHeight = keyboardFrame.height
        textFieldBottomConstraint.update(inset: keyboardHeight)
        
        UIView.animate(withDuration: duration) {
            self.layoutIfNeeded()
        }
    }
    
    @objc private func keyboardWillHide(_ notification: Notification) {
        guard let userInfo = notification.userInfo,
              let duration = userInfo[UIResponder.keyboardAnimationDurationUserInfoKey] as? TimeInterval
        else { return }
        
        textFieldBottomConstraint.update(inset: 0)
        
        UIView.animate(withDuration: duration) {
            self.layoutIfNeeded()
        }
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
}
