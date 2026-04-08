//
//  InteractionView.swift
//  Pods
//
//  Created by 邓锦龙 on 2026/3/23.
//

import UIKit

let colorGreen = UIColor(red: 0.114, green: 0.671, blue: 0.271, alpha: 1.0)   // #1DAB45
let colorRed = UIColor(red: 1.0, green: 0.239, blue: 0.298, alpha: 1.0)

class OptionView: UIView {
    private let icon = UIImageView(image: SourceHelper.image(with: "ic_unselect"))
    let text = UILabel()
    
    var selected = false
    var index = 0
    var themeColor: UIColor?
    
    func update() {
        if (selected) {
            icon.image = SourceHelper.image(with: "ic_select")?.withTintColor(themeColor!)
            text.textColor = themeColor!
        } else {
            text.textColor = UIColor.white
            icon.image = SourceHelper.image(with: "ic_unselect")
        }
    }
    
    func showBorder(_ show: Bool) {
        if (show) {
            layer.borderColor = colorGreen.cgColor
            layer.borderWidth = 1
        } else {
            layer.borderColor = nil
            layer.borderWidth = 0
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        
        text.textColor = UIColor.white
        text.font = .boldSystemFont(ofSize: 14)
        text.text = ""
        text.numberOfLines = 0
        
        addSubview(icon)
        addSubview(text)
        
        icon.snp.makeConstraints {
            $0.left.equalToSuperview()
            $0.top.greaterThanOrEqualToSuperview()
            $0.bottom.lessThanOrEqualToSuperview()
            $0.centerY.equalToSuperview()
            $0.height.equalTo(16)
            $0.width.equalTo(16)
        }
        text.snp.makeConstraints {
            $0.top.equalToSuperview().offset(8)
            $0.bottom.equalToSuperview().offset(-8)
            $0.left.equalTo(icon.snp.right).offset(10)
            $0.right.equalToSuperview()
        }
        text.setContentHuggingPriority(.required, for: .horizontal)
        text.setContentCompressionResistancePriority(.required, for: .horizontal)
        text.setContentHuggingPriority(.required, for: .vertical)
        text.setContentCompressionResistancePriority(.required, for: .vertical)

        
        isUserInteractionEnabled = true
    }

    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
}


class ResultView: UIView {
    private let resultIcon = UIImageView(image: SourceHelper.image(with: "ic_wrong"))
    private let resultStatus = UILabel()
    private let resultText = UILabel()
    
    var statusCorrect = "正确"
    var statusError = "错误"

    func update(_ ans: InteractionAnswerEntity) {
        if (ans.isCorrect == 1) {
            resultIcon.image = SourceHelper.image(with: "ic_correct")
            resultStatus.textColor = colorGreen
            resultStatus.text = statusCorrect
        } else {
            resultIcon.image = SourceHelper.image(with: "ic_wrong")
            resultStatus.textColor = colorRed
            resultStatus.text = statusError
        }
        resultText.text = ans.hint
        sizeToFit()
    }
    
    func updateHint(_ ans: String) {
        resultText.text = resultText.text?.replacingOccurrences(of: "$1", with: ans)
        sizeToFit()
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        resultStatus.textColor = UIColor.red
        resultStatus.font = .boldSystemFont(ofSize: 14)
        resultStatus.text = "错误"
        
        resultText.textColor = UIColor(white: 0.88, alpha: 1)
        resultText.font = .boldSystemFont(ofSize: 14)
        resultText.text = "试试其他位置吧"
        
        addSubview(resultIcon)
        addSubview(resultStatus)
        addSubview(resultText)
        
        resultIcon.snp.makeConstraints {
            $0.left.equalToSuperview().offset(0)
            $0.right.equalTo(resultStatus.snp.left).offset(-10)
            $0.centerY.equalToSuperview()
            $0.height.equalTo(16)
            $0.width.equalTo(16)
        }
        resultStatus.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.right.equalTo(resultText.snp.left).offset(-10)
        }
        resultText.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.right.equalToSuperview().offset(0)
        }
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
}

class TitleView: UIView {
    let titleIcon = UIImageView(image: SourceHelper.image(with: "ic_hand_touch"))
    let titleLabel = UILabel()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
         
        // title
        titleLabel.textColor = .white
        titleLabel.font = .boldSystemFont(ofSize: 16)
        titleLabel.text = "位置点选"
        
        addSubview(titleIcon)
        addSubview(titleLabel)
        
        titleIcon.snp.makeConstraints {
            $0.left.equalToSuperview()
            $0.centerY.equalToSuperview()
            $0.height.equalTo(20)
            $0.width.equalTo(20)
        }
        titleLabel.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.left.equalTo(titleIcon.snp.right).offset(12)
            $0.right.equalToSuperview()
        }
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
}



