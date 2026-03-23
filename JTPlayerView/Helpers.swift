//
//  InteractionView.swift
//  Pods
//
//  Created by 邓锦龙 on 2026/3/23.
//

import UIKit

func getAttrStr(_ title: String, _ color: UIColor) -> NSAttributedString {
    return NSAttributedString(
        string: title,
        attributes: [
            .foregroundColor: color,
            .font: UIFont.systemFont(ofSize: 14)
        ]
    )
}

public class SourceHelper: NSObject {
    static var bundle: Bundle = {
        let tempBundle = Bundle(for: SourceHelper.classForCoder())
        if let path = tempBundle.path(forResource: "jtplayerview", ofType: "bundle"),
           let temp = Bundle(path: path)
        {
            return temp
        }
        return tempBundle
    }()

    static func image(with name: String) -> UIImage? {
        return UIImage(named: name, in: bundle, compatibleWith: nil)
    }
}

 

// 采样视频主要颜色
func dominantColor(from image: UIImage) -> UIColor {
    let size = CGSize(width: 50, height: 50)

    UIGraphicsBeginImageContext(size)
    image.draw(in: CGRect(origin: .zero, size: size))
    guard let smallImage = UIGraphicsGetImageFromCurrentImageContext() else {
        return .white
    }
    UIGraphicsEndImageContext()

    guard let cgImage = smallImage.cgImage else { return .white }

    let width = cgImage.width
    let height = cgImage.height

    let bytesPerPixel = 4
    let bytesPerRow = bytesPerPixel * width
    let rawData = UnsafeMutablePointer<UInt8>.allocate(capacity: height * width * 4)

    let colorSpace = CGColorSpaceCreateDeviceRGB()
    let context = CGContext(data: rawData,
                            width: width,
                            height: height,
                            bitsPerComponent: 8,
                            bytesPerRow: bytesPerRow,
                            space: colorSpace,
                            bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue)!

    context.draw(cgImage, in: CGRect(x: 0, y: 0, width: width, height: height))

    var colorCount: [Int: Int] = [:]

    for x in 0..<width {
        for y in 0..<height {
            let index = y * bytesPerRow + x * bytesPerPixel

            let r = Int(rawData[index])
            let g = Int(rawData[index + 1])
            let b = Int(rawData[index + 2])
            let a = Int(rawData[index + 3])

            if a < 50 { continue }

            let brightness = (r + g + b) / 3
            if brightness > 240 || brightness < 30 { continue }

            let key = ((r / 32) << 10) | ((g / 32) << 5) | (b / 32)
            colorCount[key, default: 0] += 1
        }
    }

    rawData.deallocate()

    guard let max = colorCount.max(by: { $0.value < $1.value }) else {
        return .white
    }

    let key = max.key
    let r = ((key >> 10) & 0x1F) * 32
    let g = ((key >> 5) & 0x1F) * 32
    let b = (key & 0x1F) * 32

    return UIColor(red: CGFloat(r)/255,
                   green: CGFloat(g)/255,
                   blue: CGFloat(b)/255,
                   alpha: 1)
}

// 计算视频显示留白宽度
func calculateBlank(
    viewSize: CGSize,
    videoSize: CGSize
) -> (horizontal: CGFloat, vertical: CGFloat) {

    let viewRatio = viewSize.width / viewSize.height
    let videoRatio = videoSize.width / videoSize.height

    if videoRatio > viewRatio {
        // 👉 视频更“宽”（横向填满）
        let scale = viewSize.width / videoSize.width
        let scaledHeight = videoSize.height * scale

        let blank = viewSize.height - scaledHeight
        return (horizontal: 0, vertical: blank)

    } else {
        // 👉 视频更“高”（竖向填满）
        let scale = viewSize.height / videoSize.height
        let scaledWidth = videoSize.width * scale

        let blank = viewSize.width - scaledWidth
        return (horizontal: blank, vertical: 0)
    }
}


extension UIColor {
    convenience init(hex: String, alpha: CGFloat = 1.0) {
        var hexSanitized = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        hexSanitized = hexSanitized.replacingOccurrences(of: "#", with: "")
        var rgb: UInt64 = 0
        Scanner(string: hexSanitized).scanHexInt64(&rgb)
        let red = CGFloat((rgb & 0xFF0000) >> 16) / 255.0
        let green = CGFloat((rgb & 0x00FF00) >> 8) / 255.0
        let blue = CGFloat(rgb & 0x0000FF) / 255.0
        self.init(red: red, green: green, blue: blue, alpha: alpha)
    }
    
    func inverted() -> UIColor {
        var r: CGFloat = 0
        var g: CGFloat = 0
        var b: CGFloat = 0
        var a: CGFloat = 0
        
        guard self.getRed(&r, green: &g, blue: &b, alpha: &a) else {
            return self
        }
        
        return UIColor(
            red: 1.0 - r,
            green: 1.0 - g,
            blue: 1.0 - b,
            alpha: a
        )
    }
}
