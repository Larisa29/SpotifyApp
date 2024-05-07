from spyne import ComplexModel, Integer, String, Array

from models.dto.roleDTO import RoleDTO

class UserDTO(ComplexModel):
    user_id = Integer
    user_name = String()
    user_roles = Array(RoleDTO)

    def __init__(self, id, name, roles):
        super(UserDTO, self).__init__()
        self.user_id = id
        self.user_name = name
        self.user_roles = roles