package me.ako.yts.domain.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

class Base {
    class PairLiveData<F, S>(first: LiveData<F>, second: LiveData<S>) :
        MediatorLiveData<Pair<F?, S?>>() {
        init {
            addSource(first) {
                value = Pair(it, second.value)
            }
            addSource(second) {
                value = Pair(first.value, it)
            }
        }
    }

    class CombineLiveData<F, S, R>(
        first: LiveData<F>,
        second: LiveData<S>,
        combine: (F?, S?) -> R
    ) : MediatorLiveData<R>() {
        init {
            addSource(first) {
                value = combine(it, second.value)
            }
            addSource(second) {
                value = combine(first.value, it)
            }
        }
    }
}

/**
 * Here's an overview of what the code does:
 *
 * 1. It takes a generic parameter T, which represents the type of the existing object
 * and the new object.
 *
 * 2. It obtains the member properties of the existing object using this::class.memberProperties.
 *
 * 3. It obtains the member properties of the new object using newObject::class.memberProperties.
 *
 * 4. It iterates over each property in the existing object.
 *
 * 5. For each property, it searches for a corresponding property in the new object with
 * the same name using newProperties.find { it.name == existingProperty.name }.
 *
 * 6. If a corresponding property is found and it is both mutable and nullable, it proceeds
 * to update the existing object's property with the value from the new object.
 *
 * 7. To do this, it retrieves the underlying Java fields for both the existing property and
 * the new property using existingProperty.javaField and newProperty.javaField.
 *
 * 8. It ensures that both fields are accessible by setting isAccessible to true.
 *
 * 9. It retrieves the new value from the new object's field using newField.get(newObject).
 *
 * 10. If the new value is not null, it sets the value of the existing object's field using
 * existingField.set(this, newValue).
 *
 * In summary, this extension function allows you to update the properties of an object
 * with non-null values from another object, effectively updating the null properties of
 * the existing object with corresponding non-null properties from the new object.
 **/
fun <T : Any> T.updateIfNull(newObject: T) {
    // Get the member properties of the existing object
    val existingProperties = this::class.memberProperties

    // Get the member properties of the new object
    val newProperties = newObject::class.memberProperties

    // Iterate over the existing properties
    for (existingProperty in existingProperties) {
        // Find the corresponding property in the new object
        val newProperty = newProperties.find { it.name == existingProperty.name }

        // Check if the property is mutable and nullable
        if (newProperty is KMutableProperty1<*, *> && newProperty.returnType.isMarkedNullable) {
            // Get the underlying Java fields for reflection
            val existingField = existingProperty.javaField
            val newField = newProperty.javaField

            // Ensure both fields are accessible
            if (existingField != null && newField != null) {
                existingField.isAccessible = true
                newField.isAccessible = true

                // Get the new value from the new object's field
                val newValue = newField.get(newObject)

                // Update the existing object's field if the new value is not null
                if (newValue != null) {
                    existingField.set(this, newValue)
                }
            }
        }
    }
}