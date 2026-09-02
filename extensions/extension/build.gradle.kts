extension {
    name = "extensions/extension.mpe"
}

android {
    namespace = "io.github.nai64.extension"
}

dependencies {
    // Tells the framework that these classes belong to Morphe Extensions
    compileOnly("app.morphe:morphe-extensions-library:1.8.0") 
}
