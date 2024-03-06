package com.example.news

import com.example.news.manager.NewsManager
import com.example.news.ui.contracts.NewsActivityInterface
import com.example.news.ui.contracts.NewsFragmentInterface
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class NewsManagerTest {

    @Mock
    private lateinit var mockView: NewsFragmentInterface

    @Mock
    private lateinit var mockActivity: NewsActivityInterface

    private lateinit var newsManager: NewsManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        newsManager = NewsManager(mockView)
    }

    @Test
    fun `test getLatestNews when network is available`() {
        val countryCode = "us"

        `when`(mockView.isNetworkAvailable()).thenReturn(true)
        `when`(mockActivity.getSelectedCountryCode()).thenReturn(countryCode)

        newsManager.getLatestNews(countryCode)
    }

    @Test
    fun `test getLatestNews when network is not available`() {
        val countryCode = "us"

        `when`(mockView.isNetworkAvailable()).thenReturn(false)
        `when`(mockActivity.getSelectedCountryCode()).thenReturn(countryCode)

        newsManager.getLatestNews(countryCode)
    }

//    @Test
//    fun `test getLatestNews when country code changes and network is available`() {
//        val initialCountryCode = "us"
//        val newCountryCode = "uk"
//
//        `when`(mockView.isNetworkAvailable()).thenReturn(true)
//        `when`(mockActivity.getSelectedCountryCode())
//            .thenReturn(initialCountryCode)
//            .thenReturn(newCountryCode)
//
//        // Call getLatestNews with the initial country code
//        newsManager.getLatestNews(initialCountryCode)
//
//        // Change the country code
//        newsManager.getLatestNews(newCountryCode)
//    }
//
//    @Test
//    fun `test getLatestNews when country code changes and network is not available`() {
//        val initialCountryCode = "us"
//        val newCountryCode = "uk"
//
//        `when`(mockView.isNetworkAvailable()).thenReturn(false)
//        `when`(mockActivity.getSelectedCountryCode())
//            .thenReturn(initialCountryCode)
//            .thenReturn(newCountryCode)
//
//        // Call getLatestNews with the initial country code
//        newsManager.getLatestNews(initialCountryCode)
//
//        // Change the country code
//        newsManager.getLatestNews(newCountryCode)
//    }
}
