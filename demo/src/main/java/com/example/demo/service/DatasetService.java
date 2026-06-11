package com.example.demo.service;

import com.example.demo.pojo.Dataset;
import com.example.demo.pojo.IfDataset;

import java.util.List;

public interface DatasetService {
    List<Dataset> getDatasets();

    void addDataset(Dataset dataset);

    void deleteById(Integer id);

    List<Dataset> selectDataset(IfDataset ifDataset);

    void updDataset(Dataset dataset);
}
