import SwiftUI
import shared

@main
struct iOSApp: App {
    
    init() {
        OG.create()
        OG.initialize()
    }
    
	var body: some Scene {
		WindowGroup {
            CounterView(counterModel:OG[CounterModel.self])
		}
	}
}
