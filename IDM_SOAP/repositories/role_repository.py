from models.role_orm import Role
from base.sql_base import Session
def get_roles():
    session = Session()
    roles = session.query(Role).all()

    return roles
def get_role_by_id(id):
    session = Session()
    role = session.query(Role).get(id)

    return role
def get_role_by_name(role_name):
    session = Session()
    role = session.query(Role).filter(Role.value == role_name)

    return role.first()
def create_role(values):
    session = Session()
    role = Role(values)
    try:
        session.add(role)
        session.commit()
    except Exception as exc:
        print(f"Failed to add role - {exc}")
    return role
