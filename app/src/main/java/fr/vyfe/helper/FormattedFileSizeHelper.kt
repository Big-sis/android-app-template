package fr.vyfe.helper

     fun sizeFormat(size: Long): String {
        val suffixes = arrayOf("octets", "Ko", "Mo", "Go", "To")
        var tmpSize = size.toDouble()
        var i = 0

        while (tmpSize >= 1024) {
            tmpSize /= 1024.0
            i++
        }

        tmpSize *= 100.0
        tmpSize = (tmpSize + 0.5).toInt().toDouble()
        tmpSize /= 100.0

        return tmpSize.toString() + " " + suffixes[i]
    }


