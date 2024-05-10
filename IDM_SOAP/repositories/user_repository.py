import spyne

from models.dto.roleDTO import RoleDTO
from models.dto.user_dto import UserDTO
from models.user_orm import User
from base.sql_base import Session

def get_users():
    session = Session()
    users = session.query(User).all()
    return users

def get_user_by_id(id):
    session = Session()
    user = session.query(User).get(id)
    return user

def get_user_by_username(username):
    session = Session()
    try:
        result = session.query(User).filter(User.username == username).first()
        return result
    except Exception as exc:
        print(f"Failed to GET user - {exc}")

def create_user(username, password, role):
    session = Session().object_session(role)
    check_user = session.query(User).filter_by(username=username, password=password).first()
    if check_user:
        raise spyne.model.fault.Fault(faultcode='Client', faultstring=f"User with username={username} and password={password} already exists!")

    user = User(username, password)
    user.roles.append(role)
    try:
        session.add(user)
        session.commit()
        roles_dto = [RoleDTO(role.id, role.value) for role in user.roles]
        user_dto = UserDTO(user.id, user.username, roles_dto)
        return user_dto
    except Exception as exc:
        session.rollback()
        print(f"Failed to add user - {exc}")
        raise spyne.Fault(faultcode='Server', faultstring="Could not save this user")

def delete_user_by_username(username):
    session = Session()
    try:
        session.query(User).filter(User.username == username).delete(synchronize_session= False)
        session.commit()
    except Exception as exc:
        session.rollback()
        print(f"Failed to DELETE user - {exc}")
    return f"User {username } deletd!"

def update_username_for_user(id, updated_name):
    session = Session()
    try:
        session.query(User).filter(User.id == id).update({User.username: updated_name})
        session.commit()
    except Exception as exc:
        session.rollback()
        return f"Failed to update user username - {exc}"

    return True

def update_password_for_user(user, updated_password):
    session = Session().object_session(user)
    user.password = updated_password
    try:
        session.add(user)
        session.commit()
    except Exception as exc:
        session.rollback()
        print(f"Failed to update user password - {exc}")

    return True