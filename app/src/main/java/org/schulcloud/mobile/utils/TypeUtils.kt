package org.schulcloud.mobile.utils

infix fun Boolean.implies(other: Boolean) = !this || other
