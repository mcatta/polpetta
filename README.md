# Polpetta

It's "another" MVI library but with a funny name.
With this library you will be able to mutate your State by using a Action defined by using DSL.

### How it works
You application's State must extend `State` and your action the `Action` class. Every `Action` can prompt a `Reducer` which basically manipulate your State.

```kotlin
data class CounterState(val counter: Int) : State

sealed interface CounterAction : Action {
    object Increase : CounterAction
    object Decrease : CounterAction
    data class Set(val n: Int) : CounterAction
    object DoNothing : CounterAction
}
```

This is you State and Actions definition, now you need to write a `StateStore` which basically will persist your state and your `Action/Reducer` definition.

```kotlin
class CounterStore(scope: CoroutineScope) : StateStore<CounterAction, CounterState>(
    coroutineScope = scope,
    initialState = CounterState(0),
    reducerFactory =  {
        on<CounterAction.Decrease> {
            reducer { state -> state.mutate { copy(counter = counter - 1) } }
        }
        on<CounterAction.Increase> {
            reducer { state -> state.mutate { copy(counter = counter + 1) } }
        }
        on<CounterAction.Set> { action ->
            reducer { state -> state.mutate { copy(counter = action.n) } }
        }
        on<CounterAction.DoNothing> {
            reducer { state -> state.nothing() }
        }
        // ...
    }
)
```

The reducer supports two types of operation:
```kotlin
reducer { state -> state.nothing() }
```
which basically doesn't change the state

```kotlin
reducer { state -> state.mutate { copy(counter = counter + 1) } }
```
which mutate the state (Note: your state must be `data class` in order to copy it)

### Next?
- Support other kind of mutations
- Side effects