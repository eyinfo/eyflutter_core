//
//  EmulatorJudge.swift
//  eyflutter_core
//
//  Created by 李敬欢 on 2021/12/26.
//

import Foundation

class EmulatorJudge: NSObject {
    
    struct Platform {
        static let isSimulator: Bool = {
            var isSim = false
            #if arch(i386) || arch(x86_64)
                isSim = true
            #endif
            return isSim
        }()
    }
    
    func isEmulator() -> Bool {
        return Platform.isSimulator
    }
}
