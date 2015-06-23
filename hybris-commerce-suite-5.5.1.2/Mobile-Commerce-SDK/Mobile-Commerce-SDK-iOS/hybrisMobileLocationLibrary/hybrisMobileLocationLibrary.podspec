# hybrisMobileLocationLibrary.podspec
# [y] hybris Platform
#
# Copyright (c) 2000-2015 hybris AG
# All rights reserved.
#
# This software is the confidential and proprietary information of hybris
# ("Confidential Information"). You shall not disclose such Confidential
# Information and shall use it only in accordance with the terms of the
# license agreement you entered into with hybris.
#
# Be sure to run `pod lib lint hybrisMobileLocationLibrary.podspec' to ensure this is a
# valid spec and remove all comments before submitting the spec.
#
# Any lines starting with a # are optional, but encouraged
#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = "hybrisMobileLocationLibrary"
  s.version          = "5.5.0"
  s.summary          = "Hybris Mobile Location Library that contains SDK functionality to display located items on a map"
  s.description      = <<-DESC
                        Hybris Mobile Location Library.
                        You should reference this pod from your app and use the domain specific library interface (e.g. b2b) in your controllers.
                       DESC
  s.homepage         = "https://wiki.hybris.com/display/b2b/IOS+Architecture+-+5.5"
  s.license          = 'Commercial'
  s.author           = { "Playmobile IOS Team Hybris" => "yann.bouschet@hybris.com" }
  s.source           = { :git => "https://stash.hybris.com/projects/MOBILE/repos/b2bios/browse/hybrisMobileLocationLibrary", :tag => s.version.to_s }
  s.platform     = :ios, '7.0'
  s.requires_arc = true
  s.source_files = 'Pod/Classes'
  s.resource_bundles = {
    'hybrisMobileLocationLibrary' => ['Pod/Assets/*.png']
  }
end
