Pod::Spec.new do |spec|
    spec.name = 'JTPlayerKit'
    spec.version = '1.1.0'
    spec.license = { :type => 'MIT' }
    spec.homepage = 'http://code.jms.com/mobile/ios-tencent-video'
    spec.authors = { 'zhoupushan' => 'zhoupushan@jtexpress.com' }
    spec.summary = 'JT播放器'
    spec.source = { :git => 'http://code.jms.com/mobile/ios-tencent-video.git', :tag => spec.version }

    spec.ios.deployment_target = '12.4'
    spec.requires_arc = true
    spec.dependency 'SDWebImage'
    spec.dependency 'Masonry'
    spec.static_framework = true
    spec.default_subspec = 'Player'
    spec.frameworks = [
      "CoreTelephony",
      "CoreGraphics",
      "Accelerate",
      'MetalKit',
      "AVFoundation",
      "AVKit",
      "AssetsLibrary",
      "SystemConfiguration",
      "GLKit",
      "CoreServices",
      "ReplayKit",
      "AudioToolbox",
      "VideoToolbox"]
    spec.libraries = [
      "z",
      "resolv",
      "iconv",
      "stdc++",
      "c++",
      "sqlite3"
    ]
    
    spec.subspec "Player" do |s|
        s.source_files = 'JTPlayerKit/**/*.{h,m,mm}'
        s.private_header_files = 'JTPlayerKit/Utils/TXBitrateItemHelper.h', 'JTPlayerKit/Views/SuperPlayerView+Private.h'
        s.resource = 'JTPlayerKit/Resource/*'
        s.dependency "TXLiteAVSDK_Professional"
    end
end


