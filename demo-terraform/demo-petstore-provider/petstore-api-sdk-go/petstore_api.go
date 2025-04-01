// petstore api sdk封装
package petstore

import (
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"strings"
)

type HttpResponse struct {
	ErrorCode    int    `json:"errorCode"`
	ErrorMessage string `json:"errorMessage"`
	Data         any    `json:"data"`
}

type Pet struct {
	// pet id
	Id int64 `json:"id"`

	// pet名称
	Name string `json:"name"`

	// pet年龄
	Age int `json:"age"`

	// pet创建时间
	CreateTime string `json:"createTime"`
}

type PetApi interface {
	// 创建一个pet
	Create(options PetCreateOptions) (*Pet, error)

	// 删除一个pet
	Delete(petId int64) error

	// 更新一个pet
	Update(petId int64, options PetUpdateOptions) (*Pet, error)

	// 读取一个pet
	Read(petId int64) (*Pet, error)
}

type PetClient struct {
	Host string
	Port int

	HttpClient *http.Client
}

func NewPetClient(host string, port int) (*PetClient, error) {
	return &PetClient{
		Host:       host,
		Port:       port,
		HttpClient: &http.Client{},
	}, nil
}

// 实现PetApi
func (p *PetClient) Create(options PetCreateOptions) (*Pet, error) {
	values := url.Values{}
	values.Set("name", options.Name)
	values.Set("age", fmt.Sprintf("%d", options.Age))

	requestUrl := fmt.Sprintf("http://%s:%d/api/v1/pet/add?lang=zh-cn", p.Host, p.Port)

	request, err := http.NewRequest("POST", requestUrl, strings.NewReader(values.Encode()))
	if err != nil {
		return nil, err
	}
	request.Header.Set("Content-Type", "application/x-www-form-urlencoded")

	response, err := p.HttpClient.Do(request)
	if err != nil {
		return nil, err
	}
	defer response.Body.Close()

	datum, err := io.ReadAll(response.Body)
	if err != nil {
		return nil, err
	}

	var petIdFloat64 float64
	httpResponse, err := unmarshalAndCheckResponse(datum, petIdFloat64)
	if err != nil {
		return nil, err
	}

	petId := int64(httpResponse.Data.(float64))

	pet, err := p.Read(petId)
	if err != nil {
		return nil, err
	}

	return pet, nil
}

func unmarshalAndCheckResponse(datum []byte, v any) (*HttpResponse, error) {
	httpResponse := &HttpResponse{}
	httpResponse.Data = v
	err := json.Unmarshal(datum, httpResponse)
	if err != nil {
		return nil, err
	}

	if httpResponse.ErrorCode > 0 {
		return nil, errors.New(httpResponse.ErrorMessage)
	}

	return httpResponse, nil
}

func (p *PetClient) Delete(petId int64) error {
	requestUrl := fmt.Sprintf("http://%s:%d/api/v1/pet/delete?lang=zh-cn&id=%d", p.Host, p.Port, petId)

	request, err := http.NewRequest("DELETE", requestUrl, nil)
	if err != nil {
		return err
	}

	response, err := p.HttpClient.Do(request)
	if err != nil {
		return err
	}
	defer response.Body.Close()

	datum, err := io.ReadAll(response.Body)
	if err != nil {
		return err
	}

	var tempStr string
	_, err = unmarshalAndCheckResponse(datum, tempStr)
	if err != nil {
		return err
	}

	return nil
}
func (p *PetClient) Update(petId int64, options PetUpdateOptions) (*Pet, error) {
	values := url.Values{}
	values.Set("id", fmt.Sprintf("%d", petId))
	values.Set("name", options.Name)
	values.Set("age", fmt.Sprintf("%d", options.Age))
	requestUrl := fmt.Sprintf("http://%s:%d/api/v1/pet/update?lang=zh-cn", p.Host, p.Port)

	request, err := http.NewRequest("PUT", requestUrl, strings.NewReader(values.Encode()))
	if err != nil {
		return nil, err
	}
	request.Header.Set("Content-Type", "application/x-www-form-urlencoded")

	response, err := p.HttpClient.Do(request)
	if err != nil {
		return nil, err
	}
	defer response.Body.Close()

	datum, err := io.ReadAll(response.Body)
	if err != nil {
		return nil, err
	}

	// todo
	var tempStr string
	_, err = unmarshalAndCheckResponse(datum, tempStr)
	if err != nil {
		return nil, err
	}

	pet, err := p.Read(petId)
	if err != nil {
		return nil, err
	}

	return pet, nil
}
func (p *PetClient) Read(petId int64) (*Pet, error) {
	requestUrl := fmt.Sprintf("http://%s:%d/api/v1/pet/get?lang=zh-cn&id=%d", p.Host, p.Port, petId)

	request, err := http.NewRequest("GET", requestUrl, nil)
	if err != nil {
		return nil, err
	}

	response, err := p.HttpClient.Do(request)
	if err != nil {
		return nil, err
	}
	defer response.Body.Close()

	datum, err := io.ReadAll(response.Body)
	if err != nil {
		return nil, err
	}

	pet := &Pet{}
	_, err = unmarshalAndCheckResponse(datum, pet)
	if err != nil {
		return nil, err
	}

	if pet.Id <= 0 {
		return nil, nil
	} else {
		return pet, nil
	}
}

// 创建pet时提供的参数
type PetCreateOptions struct {
	// pet名称
	Name string

	// pet年龄
	Age int
}

// 修改pet时提供的参数
type PetUpdateOptions struct {
	// pet名称
	Name string

	// pet年龄
	Age int
}
