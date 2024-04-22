//
//  NFCModule.swift
//  passportNFC
//
//  Created by Sviatoslav Ivanov on 4/17/24.
//

import Foundation
import NFCPassportReader

struct PassportData {
  var DG1: [String: String]?
  var sod: String
}

@objc(NFCModule)
class NFCModule: NSObject {
  private let passportReader = PassportReader()
  private let passportUtils = PassportUtils()
  
  @objc
  static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
  
  @objc(read:withResolver:withRejecter:)
  func read(mrzKey: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
    Task { [weak self] in
      guard let self = self else { return }
      do {
        let passport = try await self.passportReader.readPassport(mrzKey: mrzKey, tags: [.DG1, .SOD])
        let dict = passport.dumpPassportData(selectedDataGroups: DataGroupId.allCases, includeActiveAuthenticationData: true)

        let dataGroup = passport.dataGroupsRead
        
        var retVal: [String: Any] = ["sod": dict["SOD"] ?? ""]
        if let elements = dataGroup.lazy.elements[.DG1] as? DataGroup1 {
          var encodedDG1 = elements.elements
          for element in elements.elements {
            encodedDG1[element.key] = Data(element.value.utf8).base64EncodedString()
          }
          retVal["DG1"] = encodedDG1
            if let data = try? JSONSerialization.data(withJSONObject: retVal, options: .prettyPrinted) {
              if let jsonString = String(data: data, encoding: .utf8) {
                resolve(jsonString)
              
            }
          }
        } else {
          reject("", "DG1 is not presented", nil)
        }
      } catch let error {
        reject("", error.localizedDescription, error)
      }
    }
  }
}

