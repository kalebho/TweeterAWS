package edu.byu.cs.tweeter.server.dao;

public class S3ImageDAOFactory implements ImageDAOFactory{

    private ImageDAO imageDAO;

    @Override
    public ImageDAO getImageDAO() {
        if (imageDAO == null) {
            return new S3ImageDAO();
        }
        else {
            return imageDAO;
        }

    }
}
