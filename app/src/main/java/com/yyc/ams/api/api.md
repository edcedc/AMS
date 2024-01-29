//单张图片
val create = RequestBody.create(MediaType.parse("multipart/form-data"), File(localMediaList[0].path))
val imgBody = MultipartBody.Part.createFormData("image", "不知传什么",  create)
@Multipart
@POST("information/saveInfoBack")
fun informationSaveInfoBack(@Query("context") context : String, @Part file : MultipartBody.Part): Observable<BaseResponseBean<DataBean>>
//多张图片
val map = HashMap<String, RequestBody>()
for (i in localMediaList.indices) {
    val create = RequestBody.create(MediaType.parse("multipart/form-data"), File(localMediaList[i].path))
    map.put("file" + i, create)
}
@Multipart
@POST("information/saveInfoBack")
fun informationSaveInfoBack(@Query("context") context : String? = null, @PartMap map : HashMap<String, RequestBody>): @JvmSuppressWildcards Observable<BaseResponseBean<Objects>>

