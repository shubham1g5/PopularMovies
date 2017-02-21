# PopularMovies
PopularMovies shows users most trending and top rates movies with ability to favourite movies and access them offline. It serves as a great example of how to use retrofit with RxJava for making HTTP requests and how to use Android's new ConstraintLayout to implement application UI.

## Setup

You will need to add the Movie DB API key in your gradle.properties file to run this project. Instructions for the same are given [here](https://developers.themoviedb.org/3/getting-started/authentication).

## Implementation Features

- PopularMovies makes use of [Android Data Binding library](https://developer.android.com/topic/libraries/data-binding/index.html) to bind model and views.
- It implements two-pane layouts for tablets using Android Fragments.
- Images are cached and loaded from network using [Picasso](http://square.github.io/picasso/).
- It uses [Retrofit2](https://square.github.io/retrofit/) to make requests to the Movie DB API.
- It also uses rx Observables to subscribe to API requests.
- Movie detail layout is implemented using [Android ConstraintLayout](https://developer.android.com/training/constraint-layout/index.html). Using ConstraintLayout greatly enhances the layout performance as it eliminates the need of nested views in layouts. Plus it's much easy to work with them than relative layouts.


