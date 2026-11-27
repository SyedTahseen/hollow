# Nothing UI migration (from vibe-nothing-ui-design)

Tokens: ui/theme/Color.kt (dark/light, signal red `#D71921` fill only), Type.kt (Doto ROND100 ≤500, Geist, Geist Mono),
Shape.kt + HollowDesign.kt (radius 6 / 8 / pill), NothingMotion.kt (ease-in-out 200/400ms, no spring, press = opacity).
Fonts in res/font (SIL OFL, licenses in /fonts-licenses). 9x9 dot glyphs: ui/components/NothingDotGlyph.kt.
Shared controls: HollowPrimaryButton, HollowOutlineButton, hollowFieldColors, NothingSegmentedBar, nothingDashedBorder.
Not built/tested here (no Gradle offline) - run `./gradlew assembleDebug` and fix any import warnings.
