package com.example.demo.mapper;

import com.example.demo.pojo.Dataset;
import com.example.demo.pojo.IfDataset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DatasetMapper {


    @Select("select a.dataset_id, a.dataset_name, a.dataset_url, a.update_time, a.upload_user_id, b.user_name from powersystem.dataset a,powersystem.user b where a.upload_user_id=b.user_id order by a.update_time desc ")
    List<Dataset> getDatasets();

    @Select("insert into powersystem.dataset(dataset_name,dataset_url,upload_user_id,update_time) values (#{datasetName},#{datasetUrl},#{uploadUserId},now())")
    void addDataset(Dataset dataset);
    @Select("delete from powersystem.dataset where dataset_id=#{id}")
    void deleteById(Integer id);
    List<Dataset> selectDataset(IfDataset ifDataset);

    @Update("update dataset set dataset_name=#{datasetName},update_time=now() where dataset_id=#{datasetId}")
    void updDataset(Dataset dataset);
}
