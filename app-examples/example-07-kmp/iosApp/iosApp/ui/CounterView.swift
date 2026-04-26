import SwiftUI
import shared

struct CounterView: View {
    
    private let counterModel: CounterModel
        
    @StateObject private var counterModelState: ObservableState<CounterState>

    init(counterModel: CounterModel) {
        self.counterModel = counterModel
        _counterModelState = counterModel.toStateObject { counterModel.state }
    }
    
    var body: some View {
        let state = counterModelState.state
        
        if state.loading {
            ProgressView()
                .scaleEffect(2)
                .padding()
        } else {
            ZStack {
                Capsule()
                    .strokeBorder(Color.green, lineWidth: 40)
                    .frame(width: 260, height: 200)
                
                HStack(spacing: 20) {
                    Button(action: counterModel.decrease) {
                        ZStack {
                            Circle()
                                .fill(Color(red: 0.4, green: 0.4, blue: 0.8))
                                .frame(width: 130, height: 130)
                            
                            Text("-")
                                .font(.system(size: 30, weight: .bold))
                                .foregroundColor(.white)
                        }
                    }
                    .disabled(!state.canDecrease())
                    .opacity(state.canDecrease() ? 1.0 : 0.6)
                    
                    Text("\(state.amount)")
                        .font(.system(size: 80, weight: .medium))
                        .foregroundColor(.black)
                        .frame(width: 80)
                    
                    Button(action: counterModel.increase) {
                        ZStack {
                            Circle()
                                .fill(Color(red: 0.4, green: 0.4, blue: 0.8))
                                .frame(width: 130, height: 130)
                            
                            Text("+")
                                .font(.system(size: 30, weight: .bold))
                                .foregroundColor(.white)
                        }
                    }
                    .disabled(!state.canIncrease())
                    .opacity(state.canIncrease() ? 1.0 : 0.6)
                }
            }
            .padding()
        }
    }
}

//struct ContentView_Previews: PreviewProvider {
//    static var previews: some View {
//        ContentView(counterModel: OG.shared[CounterModel.self])
//    }
//}
