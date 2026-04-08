//
//  InteractionView.swift
//  Pods
//
//  Created by 邓锦龙 on 2026/3/23.
//

import UIKit
import SnapKit


public class InteractionAudioView: UIView {
    
    // MARK: - UI
    private let titleLabel = UILabel()
    private let timeLabel = UILabel()
    private let nextLabel = UILabel()
    private let icon = UIImageView()
    private let line = UIView()
    private let loading = UIActivityIndicatorView()
    private var themeColor = UIColor.gray

    var entity: InteractionEntity?

    func showInteraction(data: InteractionEntity) {
        if (data.interactionType != .voiceGuidance) {
            isHidden = true
            return
        }
        entity = data
        themeColor = UIColor(hex: data.themeColor)
        icon.image = SourceHelper.image(with: "ic_play")?.withTintColor(themeColor)
        isHidden = false
        nextLabel.textColor = themeColor
        nextLabel.text = ""
        nextLabel.isHidden = true
        icon.snp.remakeConstraints {
            $0.centerY.equalToSuperview()
            $0.right.equalToSuperview().inset(20)
            $0.height.equalTo(20)
            $0.width.equalTo(20)
        }
        if let text = data.actionTxt.split(separator: ",").first {
            titleLabel.text = String(text)
        }
        loading.isHidden = true
        loading.startAnimating()
    }
    
    func updateAnswer(answer: InteractionAnswerEntity?) {
        guard let answer = answer else {
            timeLabel.text = "00:00"
            return
        }
        timeLabel.text = answer.hint
        icon.image = SourceHelper.image(with: "ic_pause")?.withTintColor(themeColor)
        loading.isHidden = true
        loading.stopAnimating()
        if (answer.isCorrect == 1) {
            nextLabel.text = entity?.nextTxt
            nextLabel.isHidden = false
            icon.snp.remakeConstraints {
                $0.centerY.equalToSuperview()
                $0.right.equalTo(nextLabel.snp.left).offset(-16)
                $0.height.equalTo(20)
                $0.width.equalTo(20)
            }
        } else {
            nextLabel.text = ""
            nextLabel.isHidden = true
            icon.snp.remakeConstraints {
                $0.centerY.equalToSuperview()
                $0.right.equalToSuperview().inset(20)
                $0.height.equalTo(20)
                $0.width.equalTo(20)
            }
        }
        if (answer.remainAttempts == 1) {
            icon.image = SourceHelper.image(with: "ic_pause")?.withTintColor(themeColor)
            if let text = entity?.actionTxt.split(separator: ",").last {
                titleLabel.text = String(text)
            }
        } else {
            icon.image = SourceHelper.image(with: "ic_play")?.withTintColor(themeColor)
            if let text = entity?.actionTxt.split(separator: ",").first {
                titleLabel.text = String(text)
            }
        }
    }

    // MARK: - Init
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = UIColor.lightGray
        layer.cornerRadius = 25
        setupUI()
        setupLayout()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupUI()
        setupLayout()
    }
    
    public override class var layerClass: AnyClass {
         return CAGradientLayer.self
     }
     
    // MARK: - Setup
    
    private func setupUI() {
        if let gradientLayer = layer as? CAGradientLayer {
            gradientLayer.colors = [UIColor(hex: "#FFEDC3").cgColor, UIColor.white.cgColor]
            gradientLayer.startPoint = CGPoint(x: 0.5, y: 0)
            gradientLayer.endPoint = CGPoint(x: 0.5, y: 1)
        }
        
        // container
        addSubview(titleLabel)
        addSubview(timeLabel)
        addSubview(nextLabel)
        addSubview(icon)
        addSubview(line)
        addSubview(loading)
        
        titleLabel.textColor = UIColor(hex: "#2B2D42")
        titleLabel.font = .boldSystemFont(ofSize: 14)
        nextLabel.font = .boldSystemFont(ofSize: 14)
        timeLabel.textColor = UIColor(hex: "#2B2D42")
        timeLabel.font = .boldSystemFont(ofSize: 14)
        line.backgroundColor = UIColor(hex: "#DEE3E8")
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(boxTapped(_:)))
        nextLabel.isUserInteractionEnabled = true
        nextLabel.addGestureRecognizer(tap)
        
        let iconTap = UITapGestureRecognizer(target: self, action: #selector(boxTapped(_:)))
        icon.isUserInteractionEnabled = true
        icon.addGestureRecognizer(iconTap)
    }
    
    @objc func boxTapped(_ gesture: UITapGestureRecognizer) {
        let delayInSeconds = 0.8
        gesture.view?.isUserInteractionEnabled = false
        DispatchQueue.main.asyncAfter(deadline: .now() + delayInSeconds) {
            gesture.view?.isUserInteractionEnabled = true
        }
        
        if gesture.view == nextLabel {
            let it = InteractionSubmitEntity(interactionId: entity?.interactionId, interactionType: entity?.interactionType.rawValue, input: nil, selected: nil)
            do {
                let encoder = JSONEncoder()
                let jsonData = try encoder.encode(it)
                let jsonString = String(data: jsonData, encoding: .utf8)!
                NotificationCenter.default.post(name: NSNotification.Name.init(rawValue: "com.jt.sand.interaction.submit"), object: jsonString)
            } catch {
                print("编码失败: \(error)")
            }
        } else if gesture.view == icon {
            // 复用事件
            let it = InteractionSubmitEntity(interactionId: entity?.interactionId, interactionType: entity?.interactionType.rawValue, input: "togglePlay", selected: nil)
            do {
                let encoder = JSONEncoder()
                let jsonData = try encoder.encode(it)
                let jsonString = String(data: jsonData, encoding: .utf8)!
                NotificationCenter.default.post(name: NSNotification.Name.init(rawValue: "com.jt.sand.interaction.submit"), object: jsonString)
                loading.isHidden = false
                loading.startAnimating()
            } catch {
                print("编码失败: \(error)")
            }

        }
    }
    
    private func setupLayout() {
        titleLabel.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.left.equalToSuperview().inset(20)
            $0.right.equalTo(line.snp.left).offset(-16)
        }
        line.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.height.equalTo(20)
            $0.width.equalTo(1)
            $0.left.equalTo(titleLabel.snp.right).offset(16)
            $0.right.equalTo(timeLabel.snp.left).offset(-16)
        }
        timeLabel.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.left.equalTo(line.snp.right).offset(16)
            $0.right.equalTo(icon.snp.left).offset(-10)
        }
        icon.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.right.equalTo(nextLabel.snp.left).offset(-16)
            $0.height.equalTo(20)
            $0.width.equalTo(20)
        }
        loading.snp.makeConstraints {
            $0.center.equalTo(icon.snp.center)
            $0.height.equalTo(20)
            $0.width.equalTo(20)
        }
        nextLabel.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.left.equalTo(icon.snp.right).offset(16)
            $0.right.equalToSuperview().inset(20)
        }
    }
    
}
