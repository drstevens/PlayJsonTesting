PlayJsonTesting
===============

Path should be removed from JsSuccess

See `ReadsWritesSpec`.
`Node.readsWithFlatMap` incorrectly results in a `JsSuccess` with a non-empty `Path`

        [info] ReadsWritesSpec
        [info] x JsResult.flatMap should result in the same value as using applicative
        [error]  'JsSuccess(D(1,5,DValue(5.5)),)' is not equal to 'JsSuccess(D(1,5,DValue(5.5)),/d)' (ReadsWritesSpec.scala:17)
        [error] Expected: ....5)),[/d])
        [error] Actual:   ....5)),[])
