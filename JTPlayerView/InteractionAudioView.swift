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
    private let icon = UIImageView(image: SourceHelper.image(with: "ic_play"))
    private let line = UIView()
    private let loading = UIActivityIndicatorView()


    func showInteraction(data: InteractionEntity) {
        if (data.interactionType != .voiceGuidance) {
            isHidden = true
            return
        }
        isHidden = false
        titleLabel.text = data.actionTxt
        loading.isHidden = false
        loading.startAnimating()
    }
    
    func updateAnswer(answer: InteractionAnswerEntity?) {
        guard let answer = answer else {
            timeLabel.text = "00:00"
            return
        }
        timeLabel.text = answer.hint
        loading.isHidden = true
        loading.stopAnimating()
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
        addSubview(icon)
        addSubview(line)
        addSubview(loading)
        
        titleLabel.textColor = UIColor(hex: "#2B2D42")
        titleLabel.font = .boldSystemFont(ofSize: 14)
        timeLabel.textColor = UIColor(hex: "#2B2D42")
        timeLabel.font = .boldSystemFont(ofSize: 14)
        line.backgroundColor = UIColor(hex: "#DEE3E8")
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
            $0.right.equalToSuperview().inset(20)
            $0.height.equalTo(20)
            $0.width.equalTo(20)
        }
        loading.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.right.equalToSuperview().inset(20)
            $0.height.equalTo(20)
            $0.width.equalTo(20)
        }
    }
    
}
