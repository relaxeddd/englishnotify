package relaxeddd.englishnotify.common

// Used at the places where MainActivity Class is loaded via reflection
annotation class MainActivityUsed()

// Applied to MainActivity to not to forget to check MainActivityUsed annotation usages
annotation class MainActivityBeforeRefactoringWarning()
