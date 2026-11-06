package com.example

import com.example.data.remote.MarkdownParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testMarkdownParser_parseReadme() {
        val sampleReadme = """
            ## Adriana Chechik
            
            **3 Links**
            
            ## Links
            
            - [Air Tight](https://www.sample.com/video/air-tight/)
            - [Anal Reckoning](https://www.sample.com/video/anal-reckoning/)
            - [The Dinner Party](https://www.sample.com/video/the-dinner-party/)
        """.trimIndent()

        val category = MarkdownParser.parseReadme("Adriana Chechik", sampleReadme)
        assertEquals("Adriana Chechik", category.name)
        assertEquals(3, category.videos.size)
        assertEquals("Air Tight", category.videos[0].title)
        assertEquals("https://www.sample.com/video/air-tight/", category.videos[0].url)
        assertEquals("Adriana Chechik", category.videos[0].actress)
    }

    @Test
    fun testMarkdownParser_parseLinksMd() {
        val sampleLinks = """
            https://www.sample.com/video1.mp4
            https://www.sample.com/video2.mp4
        """.trimIndent()

        val videos = MarkdownParser.parseLinksMd("Alexa Payne", sampleLinks)
        assertEquals(2, videos.size)
        assertEquals("https://www.sample.com/video1.mp4", videos[0].url)
    }
}
