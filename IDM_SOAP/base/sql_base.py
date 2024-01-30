from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

Base = declarative_base()
engine = create_engine('mariadb+mariadbconnector:'
                       '//user_nou:admin@localhost:3306/POS')
Session = sessionmaker(bind=engine)
