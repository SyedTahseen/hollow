# Layout consistency pass (Hollow aligned to Outrun)

Reference: Outrun `ui/Tokens.kt`, `ui/Theme.kt` (`DesignScale`), `MainActivity.kt`.

## New tokens (`ui/theme/Dimens.kt`)
- `HollowSpace`: Xs 4 / Sm 8 / Md 12 / Lg 16 / Xl 20 / Xxl 24 / Xxxl 32, `Gutter = Lg` (was 20dp).
- `HollowSizes`: Control 48, Chip 40, Field 56, Button 48, TopBar 64, MaxContent 800, FocusMaxWidth 480.
- `HollowDisplay` + `HollowDesignScale`: app-wide Display size 80-130 % (Outrun's `DesignScale`).

## What changed
- ~400 off-grid margins / paddings / gaps (3,5,6,7,9,10,11,13,14,18,22,26,28 dp) snapped to the 4dp scale,
  and now reference `HollowSpace.*` instead of raw literals.
- Screen gutter 20dp -> 16dp everywhere; fetching screen (was 22dp) and bottom-nav capsule (was 24dp) now use it too.
- Buttons 50/52/54/56dp -> 48dp; text input 52dp -> 56dp; chips/segment rows 36/38dp -> 40dp.
- Home, Servers, Settings (overview + detail) headers share one 64dp header row, so titles sit at the same y.
- Content width caps (520/600/620dp) -> 800dp (focus screens 480dp). The old caps never applied because
  `fillMaxSize().widthIn(max)` is overridden by the incoming constraints; the order is fixed and Home is now centred.
- New Settings > Appearance > Display size stepper (persisted as `display_scale_percent`).

## Deliberately untouched
Decorative art (route map, globe, icon tiles), corner radii / `GroupedItemShapes`, bottom-nav capsule height (58dp),
fonts and sp sizes, and sub-5dp hairlines/strokes.

NOTE: this was edited without a compile (no Android SDK offline). Build once and eyeball the screens.
