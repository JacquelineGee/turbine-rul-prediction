package com.example.demo.service.impl;

import com.example.demo.mapper.DatasetMapper;
import com.example.demo.pojo.Dataset;
import com.example.demo.pojo.IfDataset;
import com.example.demo.service.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatasetServiceImpl implements DatasetService {

    @Autowired
    DatasetMapper datasetMapper;
    @Override
    public List<Dataset> getDatasets() {
        return datasetMapper.getDatasets();
    }

    @Override
    public void addDataset(Dataset dataset) {
        datasetMapper.addDataset(dataset);
    }

    @Override
    public void deleteById(Integer id) {
        datasetMapper.deleteById(id);
    }

    @Override
    public List<Dataset> selectDataset(IfDataset ifDataset){
        return datasetMapper.selectDataset(ifDataset);
    }

    @Override
    public void updDataset(Dataset dataset) {
        datasetMapper.updDataset(dataset);
    }


}
