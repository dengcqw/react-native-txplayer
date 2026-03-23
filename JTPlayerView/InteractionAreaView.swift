//
//  InteractionView.swift
//  Pods
//
//  Created by 邓锦龙 on 2026/3/23.
//

import UIKit
import SnapKit


public class InteractionAreaView: UIView {
    
    // 数据源
    var areaLayout: [AreaObj]? {
        didSet {
            setNeedsDisplay() // 数据变化重绘
        }
    }
    
    // MARK: - Init
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = .clear
        isOpaque = false
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    // 字符列表
    private let charList = ["A","B","C","D","E","F","G","H","I","J"]

    var themeColor: UIColor?
    // 边框画笔
    private var borderColor: UIColor {
        get {
            return themeColor ?? UIColor.white
        }
    }
    private let borderWidth: CGFloat = 1

    // 文字属性
    private var textColor : UIColor {
        get {
            return UIColor.black
        }
    }
    private let fontSize: CGFloat = 20
    private var blankSize: (horizontal: CGFloat, vertical: CGFloat) = (horizontal: 0, vertical: 0)

    // 计算区域
    func updateVideoSize(width: Int, height: Int) {
        blankSize = calculateBlank(viewSize: bounds.size, videoSize: CGSize(width: width, height: height))
        self.setNeedsDisplay()
    }
    
    // 计算颜色
    func updateVideoSnapShot(image: UIImage) {
//        dominantColorAsync(from: image) { color in
//            self.borderColor = color.inverted()
//            self.textColor = color.inverted()
//            self.setNeedsDisplay()
//        }
    }
    
//    func dominantColorAsync(from image: UIImage,
//                            completion: @escaping (UIColor) -> Void) {
//
//        DispatchQueue.global(qos: .userInitiated).async {
//            let color = dominantColor(from: image)   // 你的计算函数
//
//            DispatchQueue.main.async {
//                completion(color)
//            }
//        }
//    }

    // MARK: - 绘制
    public override func draw(_ rect: CGRect) {
        super.draw(rect)

        guard let ctx = UIGraphicsGetCurrentContext(),
              let areas = areaLayout else { return }

        for (index, area) in areas.enumerated() {

            let w = area.w
            let h = area.h

            // 跳过无效区域
            if w <= 0 || h <= 0 { continue }

            let left = area.x * (bounds.width - blankSize.horizontal) + blankSize.horizontal/2
            let top = area.y * (bounds.height - blankSize.vertical) + blankSize.vertical/2
            let width = w * (bounds.width - blankSize.horizontal)
            let height = h * (bounds.height - blankSize.vertical)

            let rect = CGRect(x: left, y: top, width: width, height: height)

            // 画矩形边框
            ctx.setStrokeColor(borderColor.cgColor)
            ctx.setLineWidth(borderWidth)
            ctx.stroke(rect)

            // 文本
            guard index < charList.count else { continue }
            let text = charList[index]

            let paragraphStyle = NSMutableParagraphStyle()
            paragraphStyle.alignment = .center

            let attributes: [NSAttributedString.Key: Any] = [
                .font: UIFont.systemFont(ofSize: fontSize),
                .foregroundColor: textColor,
                .paragraphStyle: paragraphStyle
            ]

            // 计算文字尺寸
            let textSize = text.size(withAttributes: attributes)

            let centerX = rect.midX
            let centerY = rect.midY

            let textRect = CGRect(
                x: centerX - textSize.width / 2,
                y: centerY - textSize.height / 2,
                width: textSize.width,
                height: textSize.height
            )

            // 绘制文字（自动居中）
            text.draw(in: textRect, withAttributes: attributes)
        }
    }
}
