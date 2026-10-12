import SwiftUI

import shared

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