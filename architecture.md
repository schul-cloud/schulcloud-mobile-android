# Architecture

- [Architecture](#architecture)
  - [General](#general)
  - [Activities and Fragments](#activities-and-fragments)
  - [Data structure](#data-structure)
    - [Repositories](#repositories)
  - [Common features](#common-features)
    - [RecyclerView](#recyclerview)
    - [HTML-Content](#html-content)
    - [Share](#share)
    - [Refresh](#refresh)
    - [Feedback to the user](#feedback-to-the-user)
      - [Success/Error](#successerror)
      - [Progress dialog](#progress-dialog)

## General

**IDE:** [Android Studio]  
**Build system:** Gradle  
**Programming language:** Kotlin  
**Primary libraries:** [Jetpack], [Realm]

- Use [DataBinding] for displaying simple data. If a more complex expression is required for preparing/formatting the data, set it from code or use a static (`@JvmStatic`) utility method in the respective Fragment/Activity/ViewModel/View.


## Activities and Fragments

All main content is displayed in fragments that inherit from [`MainFragment`][MainFragment]. This base class e.g. handles refresh and sharing actions. Configurations (title, menu items, FAB, etc.) are configured via `MainFragment#provideConfig()`.


## Data structure

### Repositories

Data is managed by so-called *repository*. They exist as singletons (Kotlin-`object`) and are invoked to perform any data-manipulating action. Repositories forward requests to a DAO or a storage. DAOs interact with the Realm-database, whereas storages wrap Android's native `SharedPreference`s.

*For an example of Repository + DAO, see the implementation of [`.models.course.CourseRepository`][CourseRepository]*  
*For an example of Repository + Storage, see the implementation of [`.models.user.UserRepository`][UserRepository]*

Syncing of data (e.g. to the HPI Schul-Cloud server) is done via sync-methods in the repository. They call a [`Job`][RequestJob] (inside [`.jobs`][.jobs]), which itself calls a method of [`.network.ApiServiceInterface`][ApiServiceInterface]. The retrieved data is then stored in Realm using [`Sync`][Sync].

*For an example of Job + Sync, see the implementation of [`.jobs.ListUserCoursesJob`][ListUserCoursesJob]*


## Common features

### RecyclerView

Implementing a `RecyclerView.Adapter` is pretty simple, and we've made it even easier! Just extend [`BaseAdapter`][BaseAdapter], and most of the boilerplate code is handled for you.

*For an example, see the implementation of [`TopicListAdapter`][TopicListAdapter]*

### HTML-Content

For displaying HTML-based formatted content, please use either [`ContentTextView`][ContentTextView] (for unformatted previews up to four lines) or [`ContentWebView`][ContentWebView] (for longer, formatted content). ContentWebView takes care of loading internal content (using authentication), correct opening of links (external vs internal) and resizes content designed for larger screens.

External links are opened using [`openUrl`][WebUtils], which creates a `CustomTab` behind the scenes.

*If the displayed content should be non-interactive, use [`PassiveWebView`][PassiveWebView] instead. It works the same but doesn't catch any click or scroll events.*


### Share

For sharing the view of an activity or fragment, just overwrite the property `url`. That's it! The base classes create a menu item handle the share action, passing the `url`-String. The current toolbar title is used as the share-`Intent`s subject.


### Refresh

To implement refreshing behaviour in an activity or fragment, simply set the property [`swipeRefreshLayout`][BaseActivity] to the `SwipeRefreshLayout` (SRL from now on) instance, and override refresh(). The base classes handle SRL styling, toggle SRL state, add a menu item for refresh and perform the actual refresh.

*For an example, see the implementation of [`CourseListFragment`][CourseListFragment]*


### Feedback to the user

#### Success/Error

Indicating a **successful** action to the user is done by calling [`showGenericSuccess`][DialogUtils], passing a string or string resource. This will create a toast.

An **error** can be shown by calling [`showGenericError`][DialogUtils], passing a string or string resource. This will create a toast, and the error message is automagically prefixed with "Error: " for consistency.

*If other ways of notifying the user of an error are available, they should generally be preferred. Examples include `@style/Content_Text.Empty` for empty content, `TextView.setError()` for inputs, etc.*

#### Progress dialog

Showing an indeterminate progress dialog is as simple as wrapping a `suspend`-function with [`withProgressDialog`][DialogUtils], passing a string or string resource shown in the dialog to inform the user.


*For an example, see the implementation of [`FileActivity#loadFile()`][FileActivity]*


[android studio]: https://developer.android.com/studio/
[jetpack]: https://developer.android.com/jetpack
[realm]: https://realm.io/
[databinding]: https://developer.android.com/topic/libraries/data-binding/
[livedata]: https://developer.android.com/topic/libraries/architecture/livedata
[viewmodel]: https://developer.android.com/topic/libraries/architecture/viewmodel
[ktx-synthetic]: https://kotlinlang.org/docs/tutorials/android-plugin.html#importing-synthetic-properties

[BaseActivity]: ./app/src/main/java/org/schulcloud/mobile/controllers/base/BaseActivity.kt
[BaseAdapter]: ./app/src/main/java/org/schulcloud/mobile/controllers/base/BaseAdapter.kt
[MainActivity]: ./app/src/main/java/org/schulcloud/mobile/controllers/main/MainActivity.kt
[MainFragment]: ./app/src/main/java/org/schulcloud/mobile/controllers/main/MainFragment.kt
[FileActivity]: ./app/src/main/java/org/schulcloud/mobile/controllers/file/FileActivity.kt
[TopicListAdapter]: ./app/src/main/java/org/schulcloud/mobile/controllers/course/TopicListAdapter.kt
[ApiServiceInterface]: ./app/src/main/java/org/schulcloud/mobile/network/ApiServiceInterface.kt
[UserRepository]: ./app/src/main/java/org/schulcloud/mobile/models/user/UserRepository.kt
[CourseRepository]: ./app/src/main/java/org/schulcloud/mobile/models/course/CourseRepository.kt
[CourseListFragment]: ./app/src/main/java/org/schulcloud/mobile/controllers/main/CourseListFragment.kt
[ListUserCoursesJob]: ./app/src/main/java/org/schulcloud/mobile/jobs/ListUserCoursesJob.kt
[ContentWebView]: ./app/src/main/java/org/schulcloud/mobile/views/ContentWebView.kt
[PassiveWebView]: ./app/src/main/java/org/schulcloud/mobile/views/PassiveWebView.kt
[ContentTextView]: ./app/src/main/java/org/schulcloud/mobile/views/ContentTextView.kt
[DialogUtils]: ./app/src/main/java/org/schulcloud/mobile/utils/DialogUtils.kt
[WebUtils]: ./app/src/main/java/org/schulcloud/mobile/utils/WebUtils.kt
