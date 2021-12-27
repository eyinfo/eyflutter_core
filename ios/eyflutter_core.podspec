#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint eyflutter_core.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'eyflutter_core'
  s.version          = '0.0.1'
  s.summary          = 'EyFlutter基础库'
  s.description      = <<-DESC
EyFlutter基础库
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.platform = :ios, '8.0'
  s.dependency 'ReachabilitySwift'
  s.dependency 'URITemplate'
  s.dependency 'SwiftyJSON', '~> 4.0'
  s.dependency 'MMKV'
  s.dependency 'Alamofire', '~> 4.9.1'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
