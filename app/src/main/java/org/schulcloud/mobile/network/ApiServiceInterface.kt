package org.schulcloud.mobile.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.schulcloud.mobile.models.AccessToken
import org.schulcloud.mobile.models.Credentials
import org.schulcloud.mobile.models.course.Course
import org.schulcloud.mobile.models.event.Event
import org.schulcloud.mobile.models.file.*
import org.schulcloud.mobile.models.homework.Homework
import org.schulcloud.mobile.models.homework.submission.Submission
import org.schulcloud.mobile.models.news.News
import org.schulcloud.mobile.models.topic.Topic
import org.schulcloud.mobile.models.user.User
import retrofit2.Call
import retrofit2.http.*


@Suppress("TooManyFunctions", "LongParameterList")
interface ApiServiceInterface {

    // Login
    @POST("authentication")
    fun createToken(@Body credentials: Credentials): Call<AccessToken>

    // User
    @GET("users/{id}")
    fun getUser(@Path("id") userId: String): Call<User>

    // Events
    @GET("calendar?all=true")
    fun listEvents(): Call<List<Event>>

    // News
    @GET("news?\$sort=createdAt:1")
    fun listUserNews(): Call<FeathersResponse<List<News>>>
    @GET("news/{id}")
    fun getNews(@Path("id") newsId: String): Call<News>

    // Course
    @GET("courses?\$populate[0]=teacherIds&\$populate[1]=userIds&\$populate[2]=substitutionIds")
    fun listUserCourses(): Call<FeathersResponse<List<Course>>>
    @GET("courses/{id}?\$populate[0]=teacherIds&\$populate[1]=userIds&\$populate[2]=substitutionIds")
    fun getCourse(@Path("id") courseId: String): Call<Course>

    @GET("lessons")
    fun listCourseTopics(@Query("courseId") courseId: String): Call<FeathersResponse<List<Topic>>>
    @GET("lessons/{id}")
    fun getTopic(@Path("id") topicId: String): Call<Topic>

    // Homework
    @GET("homework?\$populate=courseId&\$sort=dueDate:-1")
    fun listUserHomework(): Call<FeathersResponse<List<Homework>>>
    @GET("homework/{id}?\$populate=courseId&\$sort=dueDate:-1")
    fun getHomework(@Path("id") homeworkId: String): Call<Homework>

    @GET("submissions?\$populate=comments")
    fun listHomeworkSubmissions(@Query("homeworkId") homeworkId: String): Call<FeathersResponse<List<Submission>>>
    @GET("submissions/{id}")
    fun getSubmission(@Path("id") submissionId: String): Call<Submission>
    @PATCH("submissions/{id}")
    fun updateSubmission(@Path("id") submissionId: String, @Body submission: Submission): Call<Submission>

    // File
    @GET("fileStorage")
    fun listDirectoryContents(@Query("path") path: String): Call<DirectoryResponse>
    @GET("files/{id}")
    fun getFile(@Path("id") fileId: String): Call<File>
    @PATCH("files/{id}")
    fun updateFile(@Path("id") fileId: String, @Body file: File): Call<File>

    @POST("fileStorage/signedUrl")
    fun generateSignedUrl(@Body signedUrlRequest: SignedUrlRequest): Call<SignedUrlResponse>
    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>
    @PUT
    fun uploadFile(
        @Url fileUrl: String,
        @Header("content-type") contentType: String?,
        @Header("x-amz-meta-path") metaPath: String?,
        @Header("x-amz-meta-name") metaName: String?,
        @Header("x-amz-meta-flat-name") metaFlatName: String?,
        @Header("x-amz-meta-thumbnail") metaThumbnail: String?,
        @Body file: RequestBody
    ): Call<ResponseBody>
    @POST("files")
    fun persistFile(@Body file: CreateFileRequest): Call<File>

}
