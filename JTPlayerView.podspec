Pod::Spec.new do |s|
  s.name             = 'JTPlayerView'
  s.version          = '0.1.0'
  s.summary          = 'A short description of JTPlayerView.'
  s.description      = <<-DESC
TODO: Add long description of the pod here.
                       DESC

  s.homepage         = 'https://github.com/YOUR_USERNAME/JTPlayerView'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'Your Name' => 'your.email@example.com' }
  s.source           = { :git => 'https://github.com/YOUR_USERNAME/JTPlayerView.git', :tag => s.version.to_s }

  s.ios.deployment_target = '15.0'

  s.source_files = 'JTPlayerView/*.swift'
  s.resource = 'JTPlayerView/jtplayerview.bundle'
  s.dependency "SnapKit"
  s.frameworks = 'UIKit'
end
