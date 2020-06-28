package com.automattic.freshlypressed

import android.net.Uri
import com.automattic.freshlypressed.data.AuthorData
import com.automattic.freshlypressed.data.PostData
import com.automattic.freshlypressed.data.Posts
import com.automattic.freshlypressed.data.SiteData
import com.automattic.freshlypressed.domain.Post
import com.automattic.freshlypressed.domain.Site
import java.time.Instant
import java.util.*

object Fixtures {
    val authorDataList: List<AuthorData> = listOf(
        AuthorData("firstname1", "lastname1", "nicename1", "https://url1.com"),
        AuthorData("firstname2", "lastname2", "nicename2", "https://url2.com"),
        AuthorData("firstname3", "lastname3", "nicename3", "https://url3.com")
    )

    val postDataList: List<PostData> = listOf(
        PostData("title1", "exceprt1", authorDataList[0], "https://imageurl1.com", "020-06-21T09:00:26-04:00", "https://url_1.com"),
        PostData("title2", "exceprt2", authorDataList[1], "https://imageurl2.com", "020-06-22T09:00:26-04:00", "https://url_2.com"),
        PostData("title3", "exceprt3", authorDataList[2], "https://imageurl3.com", "020-06-23T09:00:26-04:00", "https://url_3.com")
    )

    val postList: List<Post> = listOf(
        Post("title1", "excerpt1", "author1", "https://imageurl1.com", Date.from(Instant.ofEpochMilli(1577840400)), "http://url1.com", "http://uri1.com"),
        Post("title2", "excerpt2", "author2", "https://imageurl2.com", Date.from(Instant.ofEpochMilli(1577926800)), "http://url2.com", "http://uri2.com"),
        Post("title3", "excerpt3", "author3", "https://imageurl3.com", Date.from(Instant.ofEpochMilli(1578013200)), "http://url3.com", "http://uri3.com")
    )

    val posts: Posts = Posts(postDataList)

    val siteData: SiteData = SiteData("name", "description", 42)

    val site: Site = Site("name", "description", 42)
}
