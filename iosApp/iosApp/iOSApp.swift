import SwiftUI

import Shared

@main
struct iOSApp: App {
    init() {
        MainViewControllerKt.initKoinIOS()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}