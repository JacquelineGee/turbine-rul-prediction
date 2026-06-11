package com.example.demo.mapper;

import com.example.demo.pojo.IfPrediction;
import com.example.demo.pojo.Prediction;
import com.example.demo.pojo.RulDataArrays;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PredictionMapper {
    @Insert("INSERT INTO predict (predict_time, labels, predictions, losses, predict_model_id, user_id, dataset_id) " +
            "VALUES (NOW(), #{labels}, #{predictions}, #{losses}, #{modelId}, #{userId}, #{datasetId})")
    void savePrediction(String labels, String predictions, String losses, String modelId, String userId,Integer datasetId);

    @Select("select a.predict_id,d.model_name,a.predict_time,a.user_id,b.user_name,c.dataset_name from predict a,user b,dataset c,model d where a.user_id=b.user_id and a.dataset_id=c.dataset_id and a.predict_model_id=d.model_id order by a.predict_time DESC ")
   // @Select("select * from predict")
    List<Prediction> allPredictions();
    @Delete("delete from predict where predict_id=#{id}")
    void deleteById(Integer id);
    @Select("select a.predict_id, d.model_name, a.predict_time, a.labels, a.predictions, a.user_id, a.losses, a.dataset_id,b.user_name,c.dataset_name from predict a,user b,dataset c,model d where predict_id=#{id} and a.user_id=b.user_id and a.dataset_id=c.dataset_id and a.predict_model_id=d.model_id")
    Prediction searchById(Integer id);

    

    List<Prediction> ifSelect(IfPrediction data);

    @Update("update model set model_use_times=model_use_times+1 where model_id=#{modelId}")
    void modelUse(String model);

    @Insert("insert into report(report_name, report_time, user_id, pdf_url) values (#{filename},now(),#{userId},#{url})")
    void savePdf(String url, String userId,String filename);

    @Select("select a.predict_id,b.user_name,c.model_name,d.dataset_name,a.predict_time from predict a,user b,model c,dataset d where a.user_id=b.user_id and a.predict_model_id=c.model_id and a.dataset_id=d.dataset_id and a.user_id=#{userId} order by a.predict_time DESC")
    List<Prediction> PredictionByUserId(String userId);
}
